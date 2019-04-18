package it.unimib.disco.bigtwine.services.jobsupervisor.domain;

import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Document(collection = "jobs")
@CompoundIndexes({
    @CompoundIndex(name = "analysis_id", def = "{ 'analysis_info.id': 1 }")
})
public class Job {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("analysis_info")
    private AnalysisInfo analysis;

    @Field("job_process")
    private JobProcess process;

    @Field("start_date")
    private Instant startDate;

    @Field("end_date")
    private Instant endDate;

    @Field("end_reason")
    private String endReason;

    public Job() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AnalysisInfo getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AnalysisInfo analysis) {
        this.analysis = analysis;
    }

    public JobProcess getProcess() {
        return process;
    }

    public void setProcess(JobProcess process) {
        this.process = process;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getEndReason() {
        return endReason;
    }

    public void setEndReason(String endReason) {
        this.endReason = endReason;
    }
}
