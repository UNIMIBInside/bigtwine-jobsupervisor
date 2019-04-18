package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.context.ContextProvider;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;

import java.util.HashMap;
import java.util.Map;

public class JobExecutableBuilderBeanLocator implements JobExecutableBuilderLocator {

    private final ContextProvider contextProvider;
    private Map<String, Class<? extends JobExecutableBuilder>> builders;

    public JobExecutableBuilderBeanLocator(ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
        this.builders = new HashMap<>();
    }

    @Override
    public JobExecutableBuilder getJobExecutableBuilder(Job job) {
        String analysisType = job.getAnalysis().getType();
        if (!builders.containsKey(analysisType)) {
            throw new JobExecutableBuilderNotFoundException();
        }

        Class<? extends JobExecutableBuilder> builderClass = this.builders.get(analysisType);

        return contextProvider.getBean(builderClass);
    }

    @Override
    public void registerJobExecutableBuilder(String analysisType, Class<? extends JobExecutableBuilder> builder) {
        this.builders.put(analysisType, builder);
    }
}
