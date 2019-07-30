package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell.ShellJobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.shell.ShellJobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelJobExecutableBuilderHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("shell")
public class JobSupervisorShellConfiguration {
    @Bean("TWITTER_NEEL")
    public JobExecutableBuilder getFlinkTwitterNeelShellJobExecutableBuilder(FlinkTwitterNeelJobExecutableBuilderHelper helper) {
        return new ShellJobExecutableBuilder(helper);
    }

    @Bean
    public JobExecutor getShellJobExecutor() {
        return new ShellJobExecutor();
    }
}
