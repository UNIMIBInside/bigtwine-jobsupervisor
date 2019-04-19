package it.unimib.disco.bigtwine.services.jobsupervisor.domain;

public class AnalysisInfo {
    private String id;
    private String type;
    private String inputType;
    private String owner;
    private String query;
    private String documentId;

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

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isQueryInputType() {
        return "query".equals(this.inputType);
    }
}
