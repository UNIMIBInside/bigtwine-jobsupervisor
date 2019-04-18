package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import it.unimib.disco.bigtwine.services.jobsupervisor.context.ContextProvider;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilderBeanLocator;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilderLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobSupervisorConfiguration {
    @Bean
    public JobExecutableBuilderLocator getJobExecutableBuilderLocator(ContextProvider contextProvider) {
        return new JobExecutableBuilderBeanLocator(contextProvider);
    }
}
