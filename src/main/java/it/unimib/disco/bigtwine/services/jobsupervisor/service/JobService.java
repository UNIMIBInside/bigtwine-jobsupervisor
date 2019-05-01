package it.unimib.disco.bigtwine.services.jobsupervisor.service;

import it.unimib.disco.bigtwine.services.jobsupervisor.client.AnalysisServiceClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;
import it.unimib.disco.bigtwine.services.jobsupervisor.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;


@Service
public class JobService {

    private final JobRepository jobRepository;
    private final AnalysisServiceClient analysisServiceClient;

    public JobService(JobRepository jobRepository, AnalysisServiceClient analysisServiceClient) {
        this.jobRepository = jobRepository;
        this.analysisServiceClient = analysisServiceClient;
    }

    @Transactional
    public Job createRunningJobForAnalysis(String analysisId) throws JobAlreadyRunningExecption {
        AnalysisInfo analysis;

        try {
            analysis = this.analysisServiceClient.findAnalysisById(analysisId);
        }catch(Exception e) {
            throw new NoSuchAnalysisException();
        }

        Optional<Job> runningJob = this.findRunningJobForAnalysis(analysisId);
        if (runningJob.isPresent()) {
            throw new JobAlreadyRunningExecption(runningJob.get());
        }

        Instant now = Instant.now();
        Job job = new Job();
        job.setAnalysis(analysis);
        job.setRunning(true);
        job.setStartDate(now);
        job.setLastUpdateDate(now);

        return this.jobRepository.insert(job);
    }

    @Transactional
    public Job updateJobProcess(String jobId, JobProcess process) {
        Job job = this.jobRepository.findById(jobId)
            .orElseThrow(NoSuchJobException::new);

        job.setProcess(process);
        job.setLastUpdateDate(Instant.now());

        return this.jobRepository.save(job);
    }

    @Transactional
    public Job endJob(String jobId, String endReason) {
        Job job = this.jobRepository.findById(jobId)
            .orElseThrow(NoSuchJobException::new);

        Instant now = Instant.now();
        job.setRunning(false);
        job.setEndReason(endReason);
        job.setEndDate(now);
        job.setLastUpdateDate(now);

        return this.jobRepository.save(job);
    }

    public Optional<Job> findRunningJobForAnalysis(String analysisId) {
        return this.jobRepository
            .findRunningJobForAnalysis(analysisId)
            .max(Comparator.comparing(Job::getLastUpdateDate));
    }

    public Job saveJobHeartbeat(String jobId, Instant timestamp) {
        Job job = this.jobRepository.findById(jobId)
            .orElseThrow(NoSuchJobException::new);

        if (job.getLastHeartbeatDate() != null && job.getLastHeartbeatDate().isAfter(timestamp)) {
            return job;
        }

        job.setLastHeartbeatDate(timestamp);
        job.setLastUpdateDate(Instant.now());

        return this.jobRepository.save(job);
    }

    public class NoSuchAnalysisException extends IllegalArgumentException {

    }

    public class JobAlreadyRunningExecption extends Exception {
        private Job runningJob;

        public JobAlreadyRunningExecption(String message) {
            super(message);
        }

        public JobAlreadyRunningExecption(String message, Job job) {
            super(message);
            this.runningJob = job;
        }

        public JobAlreadyRunningExecption(Job job) {
            this("Job already running, original runningJob id: " + job.getId(), job);
        }

        public Job getRunningJob() {
            return runningJob;
        }
    }

    public class NoSuchJobException extends IllegalArgumentException {

    }
}
