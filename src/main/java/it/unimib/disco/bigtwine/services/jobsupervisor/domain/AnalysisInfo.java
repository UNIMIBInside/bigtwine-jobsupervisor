package it.unimib.disco.bigtwine.services.jobsupervisor.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

public class AnalysisInfo implements Serializable {

    private String id;
    private String type;
    private UserInfo owner;
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

    public UserInfo getOwner() {
        return owner;
    }

    public void setOwner(UserInfo owner) {
        this.owner = owner;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }

    public String getInputType() {
        if (this.input == null) {
            return null;
        }

        return (String)this.input.get(InputKeys.TYPE);
    }

    public boolean isQueryInputType() {
        return this._checkInputType(InputType.QUERY);
    }

    public boolean isBoundingBoxesInputType() {
        return this._checkInputType(InputType.BOUNDING_BOXES);
    }

    public boolean isDatasetInputType() {
        return this._checkInputType(InputType.DATASET);
    }

    public boolean isStreamAnalysis() {
        return this.isQueryInputType() || this.isBoundingBoxesInputType();
    }

    public boolean isDatasetAnalysis() {
        return this.isDatasetInputType();
    }

    private boolean _checkInputType(@NotNull String inputType) {
        return inputType.equals(this.getInputType());
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

    public static class InputType {
        public static final String QUERY = "query";
        public static final String BOUNDING_BOXES = "bounding-boxes";
        public static final String DATASET = "dataset";
    }
}
