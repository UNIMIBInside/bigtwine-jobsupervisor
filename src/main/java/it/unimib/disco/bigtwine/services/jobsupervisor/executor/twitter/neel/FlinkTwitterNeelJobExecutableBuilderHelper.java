package it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel;

import it.unimib.disco.bigtwine.services.jobsupervisor.client.SocialsServiceClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.OAuthCredentials;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlinkTwitterNeelJobExecutableBuilderHelper implements JobExecutableBuilder.BuilderHelper {
    private final ApplicationProperties applicationProperties;
    private final SocialsServiceClient socialsServiceClient;

    public FlinkTwitterNeelJobExecutableBuilderHelper(ApplicationProperties applicationProperties, SocialsServiceClient socialsServiceClient) {
        this.applicationProperties = applicationProperties;
        this.socialsServiceClient = socialsServiceClient;
    }

    public OAuthCredentials getTwitterCredentials(Job job) throws JobExecutableBuilder.BuildException {
        if (job.getAnalysis().getOwner() == null) {
            throw new JobExecutableBuilder.BuildException("Job's analysis owner info missing");
        }

        String analysisOwner = job.getAnalysis().getOwner();
        try {
            return this.socialsServiceClient.findTwitterOAuthCredentials(analysisOwner);
        }catch (Exception e) {
            throw new JobExecutableBuilder.BuildException("Cannot retrieve twitter credentials for analysis owner: " + analysisOwner);
        }
    }

    @Override
    public List<String> buildExecutableCommand(Job job) throws JobExecutableBuilder.BuildException {
        AnalysisInfo analysis = job.getAnalysis();

        String jarName = this.applicationProperties.getTwitterNeel().getStream().getFlinkJob().getJarName();
        String className;

        if (analysis.isQueryInputType()) {
            className = this.applicationProperties.getTwitterNeel().getStream().getFlinkJob().getJarClass();
        }else {
            throw new UnsupportedOperationException();
        }

        return Arrays.asList("java", "-cp", jarName, className);
    }

    @Override
    public List<String> buildExecutableArgs(Job job) throws JobExecutableBuilder.BuildException {
        AnalysisInfo analysis = job.getAnalysis();
        OAuthCredentials credentials = getTwitterCredentials(job);
        List<String> args = Arrays.asList(
            "--analysis-id", analysis.getId(),
            "--twitter-token", credentials.getAccessToken(),
            "--twitter-token-secret", credentials.getAccessTokenSecret(),
            "--twitter-consumer-key", credentials.getConsumerKey(),
            "--twitter-consumer-secret", credentials.getConsumerSecret()
        );

        if (analysis.isQueryInputType()) {
            String streamLang = this.applicationProperties.getTwitterNeel().getStream().getDefaultLang();
            String streamSampling = String.valueOf(this.applicationProperties.getTwitterNeel().getStream().getSampling());
            Collections.addAll(args,
                "--twitter-stream-query", analysis.getQuery(),
                "--twitter-stream-lang", streamLang,
                "--twitter-stream-sampling", streamSampling
            );
        }else {
            throw new UnsupportedOperationException();
        }

        return args;
    }
}
