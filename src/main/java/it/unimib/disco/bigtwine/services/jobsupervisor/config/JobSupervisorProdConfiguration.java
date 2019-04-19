package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import io.github.jhipster.config.JHipsterConstants;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesJobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesObjectLoader;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.YamlTemplateKubernetesObjectLoader;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelJobExecutableBuilderHelper;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesJobExecutableBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!" + JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
public class JobSupervisorProdConfiguration {
    @Bean("TWITTER_NEEL")
    public JobExecutableBuilder getFlinkTwitterNeelKubernetesJobExecutableBuilder(FlinkTwitterNeelJobExecutableBuilderHelper helper) {
        // TODO: Set template file
        KubernetesObjectLoader kubernetesObjectLoader = new YamlTemplateKubernetesObjectLoader();
        return new KubernetesJobExecutableBuilder(kubernetesObjectLoader, helper);
    }

    @Bean
    public JobExecutor getJobExecutor() {
        return new KubernetesJobExecutor();
    }
}
