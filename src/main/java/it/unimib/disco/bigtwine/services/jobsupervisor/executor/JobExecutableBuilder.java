package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;

public interface JobExecutableBuilder<E extends JobExecutable> {
    void setJob(Job job);
    E build();
}
