package it.unimib.disco.bigtwine.services.jobsupervisor.executor.twitter.neel;

import it.unimib.disco.bigtwine.services.jobsupervisor.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutableBuilder;

import java.util.Arrays;
import java.util.List;

public class FlinkTwitterNeelExportJobExecutableBuilderHelper implements JobExecutableBuilder.BuilderHelper {
    private final ApplicationProperties applicationProperties;

    public FlinkTwitterNeelExportJobExecutableBuilderHelper(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public List<String> buildExecutableCommand(Job job) throws JobExecutableBuilder.BuildException {
        String javaBin, jarName;

        javaBin = this.applicationProperties
            .getTwitterNeel()
            .getExport()
            .getFlinkJob()
            .getJavaBin();
        jarName = this.applicationProperties
            .getTwitterNeel()
            .getExport()
            .getFlinkJob()
            .getJarName();

        return Arrays.asList(javaBin, "-cp", jarName);
    }

    @Override
    public List<String> buildExecutableArgs(Job job) throws JobExecutableBuilder.BuildException {
        AnalysisInfo analysis = job.getAnalysis();

        String className = this.applicationProperties
            .getTwitterNeel()
            .getExport()
            .getFlinkJob()
            .getJarClass();
        int heartbeatInterval = this.applicationProperties
            .getTwitterNeel()
            .getStream()
            .getHeartbeat();

        Object documentId = analysis
            .getExport()
            .get(AnalysisInfo.ExportKeys.DOCUMENT_ID);
        Object format = analysis
            .getExport()
            .get(AnalysisInfo.ExportKeys.FORMAT);

        if (documentId == null) {
            throw new JobExecutableBuilder.BuildException("Export document id not provided");
        }

        return Arrays.asList(
            className,
            "--job-id", job.getId(),
            "--analysis-id", analysis.getId(),
            "--document-id", String.valueOf(documentId),
            "--format", String.valueOf(format),
            "--heartbeat-interval", String.valueOf(heartbeatInterval)
        );
    }
}
