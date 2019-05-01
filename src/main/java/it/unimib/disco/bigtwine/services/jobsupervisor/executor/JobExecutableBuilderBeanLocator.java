package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.context.ContextProvider;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;

import java.util.HashMap;
import java.util.Map;

public class JobExecutableBuilderBeanLocator implements JobExecutableBuilderLocator {

    private final ContextProvider contextProvider;

    public JobExecutableBuilderBeanLocator(ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public JobExecutableBuilder<?> getJobExecutableBuilder(Job job) {
        if (job == null || job.getAnalysis() == null ||  job.getAnalysis().getType() == null) {
            return null;
        }

        String analysisType = job.getAnalysis().getType();

        try {
            return BeanFactoryAnnotationUtils.qualifiedBeanOfType(
                this.contextProvider.getBeanFactory(),
                JobExecutableBuilder.class,
                analysisType);
        }catch(NoSuchBeanDefinitionException e) {
            throw new JobExecutableBuilderNotFoundException(e.getLocalizedMessage());
        }
    }
}
