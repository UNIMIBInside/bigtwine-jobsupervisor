package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJobProcess implements JobProcess {

    private String pid;

    private Map<String, ?> extraData = new HashMap<>();

    public AbstractJobProcess() {
    }

    public AbstractJobProcess(String pid) {
        this.pid = pid;
    }

    @Override
    public String getPID() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public Map<String, ?> getExtraData() {
        return this.extraData;
    }
}
