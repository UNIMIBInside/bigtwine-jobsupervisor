package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.AbstractJobExecutableBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractKubernetesJobExecutableBuilder extends AbstractJobExecutableBuilder<KubernetesJobExecutable> {
    protected final KubernetesObjectLoader kubernetesObjectLoader;

    private Job job;

    public AbstractKubernetesJobExecutableBuilder(KubernetesObjectLoader kubernetesObjectLoader) {
        this.kubernetesObjectLoader = kubernetesObjectLoader;
    }

    public Job getJob() {
        return job;
    }

    @Override
    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public KubernetesJobExecutable build() throws BuildException {
        if (this.getJob() == null) {
            throw new BuildException("Job is null");
        }

        if (this.getJob().getAnalysis() == null) {
            throw new BuildException("Job's analysis info missing");
        }

        Object k8sObject = this.buildKubernetesObject();
        KubernetesJobExecutable executable = new KubernetesJobExecutable();
        executable.setKubernetesObjectSpec(k8sObject);

        return executable;
    }

    protected String buildKubernetesObjectName() {
        AnalysisInfo analysis = this.getJob().getAnalysis();
        return String.format("job-%s-%s", analysis.getType(), analysis.getId());
    }

    protected Object buildKubernetesObject() throws BuildException {
        String jobName = this.buildKubernetesObjectName();
        List<String> command = this.buildExecutableCommand();
        List<String> args = this.buildExecutableArgs();

        Map<String, Object> vars = new HashMap<>();
        vars.put("metadata.name", jobName);
        vars.put("spec.template.spec.containers[0].command", command);
        vars.put("spec.template.spec.containers[0].args", args);

        return this.kubernetesObjectLoader.getKubernetesObjectSpec(vars);
    }

    protected abstract List<String> buildExecutableCommand() throws BuildException;
    protected abstract List<String> buildExecutableArgs() throws BuildException;
}
