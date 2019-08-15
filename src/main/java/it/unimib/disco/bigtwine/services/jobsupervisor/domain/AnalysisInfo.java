package it.unimib.disco.bigtwine.services.jobsupervisor.domain;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class AnalysisInfo {

    private String id;
    private String type;
    private String owner;
    private Map<String, Object> input;

    public AnalysisInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }

    public boolean isQueryInputType() {
        return this._checkInputType("QUERY");
    }

    public boolean isBoundingBoxesInputType() {
        return this._checkInputType("BOUNDING_BOXES");
    }

    public boolean isDatasetInputType() {
        return this._checkInputType("DATASET");
    }

    public boolean isStreamAnalysis() {
        return this.isQueryInputType() || this.isDatasetInputType();
    }

    private boolean _checkInputType(@NotNull String inputType) {
        return this.input != null && inputType.equals(this.input.get(InputKeys.TYPE));
    }

    public static class InputKeys {
        // All
        public static final String TYPE = "type";

        // Query
        public static final String TOKENS = "tokens";
        public static final String JOIN_OPERATOR = "joinOperator";

        // Dataset
        public static final String DOCUMENT_ID = "documentId";
    }
}
