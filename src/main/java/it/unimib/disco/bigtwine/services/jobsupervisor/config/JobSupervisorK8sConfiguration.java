package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import io.github.jhipster.config.JHipsterConstants;
import io.kubernetes.client.ApiClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesJobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesJobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.KubernetesObjectLoader;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes.YamlTemplateKubernetesObjectLoader;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelExportJobExecutableBuilderHelper;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel.FlinkTwitterNeelJobExecutableBuilderHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_K8S)
public class JobSupervisorK8sConfiguration {

    private KubernetesObjectLoader createFlinkTwitterNeelKubernetesObjectLoader(ApplicationProperties props) throws IOException, URISyntaxException {
        String templateName = props.getTwitterNeel().getStream().getFlinkJob().getKubernetesTemplate();
        URI templateUri;
        if (Paths.get(templateName).isAbsolute()) {
            templateUri = new URI(templateName);
        } else {
            templateUri = IOUtils
                .resourceToURL(templateName, JobSupervisorK8sConfiguration.class.getClassLoader())
                .toURI();
        }

        File template = Paths.get(templateUri).toFile();
        if (!(template.exists() && template.isFile() && template.canRead())) {
            throw new InvalidConfigurationPropertyValueException(
                "application.twitterNeel.stream.flinkJob.kubernetesTemplate",
                templateName,
                "The given template is not accessible or it isn't a file: " + templateUri.toString()
            );
        }

        return new YamlTemplateKubernetesObjectLoader(template);
    }

    @Bean("TWITTER_NEEL")
    public JobExecutableBuilder getFlinkTwitterNeelKubernetesJobExecutableBuilder(
        FlinkTwitterNeelJobExecutableBuilderHelper helper,
        ApplicationProperties props) throws IOException, URISyntaxException {
        KubernetesObjectLoader kubernetesObjectLoader = createFlinkTwitterNeelKubernetesObjectLoader(props);

        return new KubernetesJobExecutableBuilder(kubernetesObjectLoader, helper);
    }

    @Bean("TWITTER_NEEL__EXPORT")
    public JobExecutableBuilder getFlinkTwitterNeelExportKubernetesJobExecutableBuilder(
        FlinkTwitterNeelExportJobExecutableBuilderHelper helper,
        ApplicationProperties props) throws IOException, URISyntaxException {
        KubernetesObjectLoader kubernetesObjectLoader = createFlinkTwitterNeelKubernetesObjectLoader(props);

        return new KubernetesJobExecutableBuilder(kubernetesObjectLoader, helper);
    }

    @Bean
    public ApiClient getKubernetesApiClient() {
        return new ApiClient();
    }

    @Bean
    public JobExecutor getKubernetesJobExecutor(ApplicationProperties props) {
        String namespace = props.getKubernetes().getNamespace();

        return new KubernetesJobExecutor(getKubernetesApiClient(), namespace);
    }
}
