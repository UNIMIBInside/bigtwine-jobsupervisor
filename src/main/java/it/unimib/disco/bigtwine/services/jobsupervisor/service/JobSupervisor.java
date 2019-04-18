package it.unimib.disco.bigtwine.services.jobsupervisor.service;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilderLocator;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.AnalysisStatusChangeRequestConsumerChannel;
import it.unimib.disco.bigtwine.services.jobsupervisor.messaging.AnalysisStatusChangedProducerChannel;
import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangeRequestedEvent;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.messaging.MessageChannel;

@Service
public class JobSupervisor {

    private final JobService jobService;
    private final MessageChannel statusChangeRequestsChannel;
    private final JobExecutor jobExecutor;
    private final JobExecutableBuilderLocator jobExecutableBuilderLocator;

    public JobSupervisor(
        JobService jobService,
        AnalysisStatusChangedProducerChannel channel,
        JobExecutor jobExecutor,
        JobExecutableBuilderLocator jobExecutableBuilderLocator) {
        this.jobService = jobService;
        this.statusChangeRequestsChannel = channel.analysisStatusChangedChannel();
        this.jobExecutor = jobExecutor;
        this.jobExecutableBuilderLocator = jobExecutableBuilderLocator;
    }

    @StreamListener(AnalysisStatusChangeRequestConsumerChannel.CHANNEL)
    public void newAnalysisStatusChangeRequest(AnalysisStatusChangeRequestedEvent event) {

    }
}
