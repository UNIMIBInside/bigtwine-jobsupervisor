package it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;

import java.util.List;

public interface ShellJobExecutable extends JobExecutable {
    List<String> getShellCommand();
}
