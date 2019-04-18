package it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;

public class ShellJobExecutor implements JobExecutor<ShellJobProcess, ShellJobExecutable> {
    @Override
    public boolean isRunning(ShellJobProcess process) {
        return false;
    }

    @Override
    public boolean stop(ShellJobProcess process) {
        return false;
    }

    @Override
    public ShellJobProcess execute(ShellJobExecutable executable) {
        return null;
    }

    @Override
    public boolean isSupported(JobProcess process) {
        return false;
    }
}
