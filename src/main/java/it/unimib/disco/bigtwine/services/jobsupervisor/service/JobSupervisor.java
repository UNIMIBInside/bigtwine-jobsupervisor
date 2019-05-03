package it.unimib.disco.bigtwine.services.jobsupervisor.service;

import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangedEvent;
import it.unimib.disco.bigtwine.commons.messaging.JobHeartbeatEvent;
import it.unimib.disco.bigtwine.commons.models.AnalysisStatusEnum;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.*;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.AnalysisStatusChangeRequestConsumerChannel;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.AnalysisStatusChangedProducerChannel;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangeRequestedEvent;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.JobHeartbeatConsumerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.messaging.MessageChannel;

@Service
public class JobSupervisor {

    private static final Logger log = LoggerFactory.getLogger(JobSupervisor.class);

    private final JobService jobService;
    private final MessageChannel statusChangedChannel;
    private final JobExecutor jobExecutor;
    private final JobExecutableBuilderLocator jobExecutableBuilderLocator;

    public JobSupervisor(
        JobService jobService,
        AnalysisStatusChangedProducerChannel channel,
        JobExecutor jobExecutor,
        JobExecutableBuilderLocator jobExecutableBuilderLocator) {
        this.jobService = jobService;
        this.statusChangedChannel = channel.analysisStatusChangedChannel();
        this.jobExecutor = jobExecutor;
        this.jobExecutableBuilderLocator = jobExecutableBuilderLocator;
    }

    private Job startAnalysisJob(String analysisId) throws JobExecutionException {
        Job newJob;
        try {
            newJob = this.jobService.createRunningJobForAnalysis(analysisId);
        } catch (JobService.NoSuchAnalysisException e) {
            throw new JobExecutionException(String.format("Analysis with id %s not found", analysisId));
        } catch (JobService.JobAlreadyRunningExecption e){
            throw new JobExecutionException(String.format("A job for analysis %s already running (job id %s)",
                analysisId, e.getRunningJob().getId()));
        }

        JobExecutableBuilder builder = this.jobExecutableBuilderLocator.getJobExecutableBuilder(newJob);
        builder.setJob(newJob);

        JobExecutable executable;
        try {
            executable = builder.build();
        } catch (JobExecutableBuilder.BuildException e) {
            e.printStackTrace();
            this.jobService.endJob(newJob.getId(), "Job executable cannot be created");
            throw new JobExecutionException(String.format("A job executable for analysis %s cannot be created",
                analysisId));
        }

        if (!this.jobExecutor.test(executable)) {
            throw new AssertionError(String.format("Executable is not valid for current executor, " +
                "invalid configuration. Executable type %s, Executor type: %s",
                executable.getClass(), this.jobExecutor.getClass()));
        }

        JobProcess process = null;
        try {
            @SuppressWarnings("unchecked")
            JobProcess p = this.jobExecutor.execute(executable);
            process = p;
        } catch (JobExecutor.JobExecutorException e) {
            e.printStackTrace();
        }

        if (process == null) {
            this.jobService.endJob(newJob.getId(), "Job executable cannot be launched");
            throw new JobExecutionException(String.format("Job executable for analysis %s cannot be launched",
                analysisId));
        }

        return this.jobService.updateJobProcess(newJob.getId(), process);
    }

    private Job stopAnalysisJob(String analysisId, boolean endJobIfStopFail) throws JobExecutionException {
        Job job = this.jobService
            .findRunningJobForAnalysis(analysisId)
            .orElseThrow(() -> new JobExecutionException(String.format("Running job for analysis %s not found", analysisId)));

        if (job.getProcess() == null) {
            this.jobService.endJob(job.getId(), "Job process missing");
            throw new JobExecutionException(String.format("Job executable for analysis %s has not a process associated",
                analysisId));
        }

        if (!this.jobExecutor.test(job.getProcess())) {
            throw new AssertionError(String.format("Process is not valid for current executor, " +
                    "invalid configuration. Job type %s, Executor type: %s",
                job.getProcess().getClass(), this.jobExecutor.getClass()));
        }

        boolean stopped = false;
        try {
            @SuppressWarnings("unchecked")
            boolean s = this.jobExecutor.stop(job.getProcess());
            stopped = s;
        } catch (JobExecutor.JobExecutorException e) {
            e.printStackTrace();
        }

        if (stopped) {
            job = this.jobService.endJob(job.getId(), "Job successfull stopped");
        }else {
            if (endJobIfStopFail) {
                this.jobService.endJob(job.getId(), "Job cannot be stopped, cancelled");
            }

            throw new JobExecutionException(String.format("Running job executable for analysis %s cannot be stopped",
                analysisId));
        }

        return job;
    }

    private Job cancelAnalysisJob(String analysisId) throws JobExecutionException {
        return this.stopAnalysisJob(analysisId, true);
    }

    private void notifyAnalysisStatusChange(String analysisId, AnalysisStatusEnum newStatus, boolean isUserInitiated, String failMessage) {
        AnalysisStatusChangedEvent event = new AnalysisStatusChangedEvent();
        event.setAnalysisId(analysisId);
        event.setStatus(newStatus);
        event.setUserInitiated(isUserInitiated);
        event.setMessage(failMessage);

        Message<AnalysisStatusChangedEvent> message = MessageBuilder
            .withPayload(event)
            .build();

        this.statusChangedChannel.send(message);
    }

    @StreamListener(AnalysisStatusChangeRequestConsumerChannel.CHANNEL)
    public void newAnalysisStatusChangeRequest(AnalysisStatusChangeRequestedEvent event) {
        String analysisId = event.getAnalysisId();
        boolean isUserRequested = event.isUserRequested();
        AnalysisStatusEnum desiredStatus = event.getDesiredStatus();
        AnalysisStatusEnum newStatus;
        String failMessage = null;

        if (desiredStatus == AnalysisStatusEnum.STARTED) {
            try {
                this.startAnalysisJob(analysisId);
                newStatus = AnalysisStatusEnum.STARTED;
            } catch (JobExecutionException e) {
                newStatus = AnalysisStatusEnum.FAILED;
                failMessage = e.getMessage();
                e.printStackTrace();
            }
        }else if (desiredStatus == AnalysisStatusEnum.STOPPED) {
            try {
                this.stopAnalysisJob(analysisId, false);
                newStatus = AnalysisStatusEnum.STOPPED;
            }catch (JobExecutionException e) {
                newStatus = null;
                failMessage = e.getMessage();
                e.printStackTrace();
            }
        }else if (desiredStatus == AnalysisStatusEnum.CANCELLED) {
            try {
                this.cancelAnalysisJob(analysisId);
                newStatus = AnalysisStatusEnum.CANCELLED;
            }catch (JobExecutionException e) {
                newStatus = null;
                failMessage = e.getMessage();
                e.printStackTrace();
            }
        }else {
            log.debug("Invalid change status request received {} for analysis: {} (user requested: {})", analysisId, event.getDesiredStatus(), isUserRequested);
            return;
        }

        this.notifyAnalysisStatusChange(analysisId, newStatus, isUserRequested, failMessage);
    }

    @StreamListener(JobHeartbeatConsumerChannel.CHANNEL)
    public void jobHeartbeatReceived(JobHeartbeatEvent event) {
        if (event.getJobId() == null || event.getTimestamp() == null) {
            return;
        }

        try {
            this.jobService.saveJobHeartbeat(event.getJobId(), event.getTimestamp());
        }catch (JobService.NoSuchJobException e) {
            e.printStackTrace();
        }
    }

    public class JobExecutionException extends Exception {
        public JobExecutionException(String message) {
            super(message);
        }
    }
}
