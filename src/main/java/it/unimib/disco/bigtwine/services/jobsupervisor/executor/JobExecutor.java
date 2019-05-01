package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

public interface JobExecutor<P extends JobProcess, E extends JobExecutable> {
    boolean isRunning(P process);
    boolean stop(P process);
    P execute(E executable);
    boolean test(JobProcess process);
    boolean test(JobExecutable executable);
}
