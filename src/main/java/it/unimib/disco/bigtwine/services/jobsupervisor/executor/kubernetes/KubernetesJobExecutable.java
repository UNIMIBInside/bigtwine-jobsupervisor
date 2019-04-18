package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;

public interface KubernetesJobExecutable extends JobExecutable {
    Object getKubernetesObjectSpec();
}
