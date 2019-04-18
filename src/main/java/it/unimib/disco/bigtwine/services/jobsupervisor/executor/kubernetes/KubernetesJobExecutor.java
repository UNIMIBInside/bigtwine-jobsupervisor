package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;

public class KubernetesJobExecutor implements JobExecutor<KubernetesJobProcess, KubernetesJobExecutable> {
    @Override
    public boolean isRunning(KubernetesJobProcess process) {
        return false;
    }

    @Override
    public boolean stop(KubernetesJobProcess process) {
        return false;
    }

    @Override
    public KubernetesJobProcess execute(KubernetesJobExecutable executable) {
        return null;
    }

    @Override
    public boolean isSupported(JobProcess process) {
        return process instanceof KubernetesJobProcess;
    }
}
