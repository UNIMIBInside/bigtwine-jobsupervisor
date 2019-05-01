package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;
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
    public boolean test(JobProcess process) {
        return process instanceof KubernetesJobProcess;
    }

    @Override
    public boolean test(JobExecutable executable) {
        return executable instanceof KubernetesJobExecutable;
    }
}
