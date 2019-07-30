package it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import it.unimib.disco.bigtwine.services.jobsupervisor.client.SocialsServiceClient;
import it.unimib.disco.bigtwine.services.jobsupervisor.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.OAuthCredentials;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.TwitterNeelUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlinkTwitterNeelJobExecutableBuilderHelper implements JobExecutableBuilder.BuilderHelper {
    private final ApplicationProperties applicationProperties;
    private final SocialsServiceClient socialsServiceClient;

    @SuppressWarnings("UnstableApiUsage")
    private final Escaper SHELL_ESCAPE = Escapers.builder()
        .addEscape('\'', "'\"'\"'")
        .build();

    public FlinkTwitterNeelJobExecutableBuilderHelper(ApplicationProperties applicationProperties, SocialsServiceClient socialsServiceClient) {
        this.applicationProperties = applicationProperties;
        this.socialsServiceClient = socialsServiceClient;
    }

    private OAuthCredentials getTwitterCredentials(Job job) throws JobExecutableBuilder.BuildException {
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
        String javaBin = this.applicationProperties.getTwitterNeel().getStream().getFlinkJob().getJavaBin();
        String jarName = this.applicationProperties.getTwitterNeel().getStream().getFlinkJob().getJarName();

        return Arrays.asList(javaBin, "-cp", jarName);
    }

    @Override
    public List<String> buildExecutableArgs(Job job) throws JobExecutableBuilder.BuildException {
        AnalysisInfo analysis = job.getAnalysis();
        OAuthCredentials credentials = getTwitterCredentials(job);

        String className;

        if (analysis.isQueryInputType()) {
            className = this.applicationProperties.getTwitterNeel().getStream().getFlinkJob().getJarClass();
        }else {
            throw new UnsupportedOperationException();
        }

        List<String> args = new ArrayList<>(Arrays.asList(
            className,
            "--job-id", job.getId(),
            "--analysis-id", analysis.getId(),
            "--twitter-token", credentials.getAccessToken(),
            "--twitter-token-secret", credentials.getAccessTokenSecret(),
            "--twitter-consumer-key", credentials.getConsumerKey(),
            "--twitter-consumer-secret", credentials.getConsumerSecret()
        ));

        if (analysis.isQueryInputType()) {
            String streamLang = this.applicationProperties.getTwitterNeel().getStream().getDefaultLang();
            String streamSampling = String.valueOf(this.applicationProperties.getTwitterNeel().getStream().getSampling());
            String streamHeartbeat = String.valueOf(this.applicationProperties.getTwitterNeel().getStream().getHeartbeat());
            String query = TwitterNeelUtil.flattifyAnalysisInput(analysis);

            if (query == null) {
                throw new JobExecutableBuilder
                    .BuildException(String.format("Cannot flattify analysis input: %s}", analysis.getInput()));
            }

            Collections.addAll(args,
                "--twitter-stream-query", String.format("'%s'", SHELL_ESCAPE.escape(query)),
                "--twitter-stream-lang", streamLang,
                "--twitter-stream-sampling", streamSampling,
                "--heartbeat-interval", streamHeartbeat
            );
        }else {
            throw new UnsupportedOperationException();
        }

        return args;
    }
}
