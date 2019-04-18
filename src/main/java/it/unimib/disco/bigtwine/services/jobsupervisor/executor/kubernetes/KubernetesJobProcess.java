package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.AbstractJobProcess;

public class KubernetesJobProcess extends AbstractJobProcess {

    public KubernetesJobProcess(String pid) {
        super(pid);
    }
}
