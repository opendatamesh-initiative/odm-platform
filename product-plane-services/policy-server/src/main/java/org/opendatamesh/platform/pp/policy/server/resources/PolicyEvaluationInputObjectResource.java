package org.opendatamesh.platform.pp.policy.server.resources;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PolicyEvaluationInputObjectResource {

    @JsonProperty("currentState")
    private Object currentState;

    @JsonProperty("afterState")
    private Object afterState;

    public Object getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Object currentState) {
        this.currentState = currentState;
    }

    public Object getAfterState() {
        return afterState;
    }

    public void setAfterState(Object afterState) {
        this.afterState = afterState;
    }

}
