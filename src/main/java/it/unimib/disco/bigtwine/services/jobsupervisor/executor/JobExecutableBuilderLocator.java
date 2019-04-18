package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;

public interface JobExecutableBuilderLocator {
    JobExecutableBuilder getJobExecutableBuilder(Job job);
    void registerJobExecutableBuilder(String analysisType, Class<? extends JobExecutableBuilder> builder);

    class JobExecutableBuilderNotFoundException extends IllegalArgumentException {

    }
}
