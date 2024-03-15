package org.opendatamesh.platform.pp.policy.server.services.mocks;

import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;

public class PolicyEngineValidationRequest {

    private Policy policy;

    private String inputObject;

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getInputObject() {
        return inputObject;
    }

    public void setInputObject(String inputObject) {
        this.inputObject = inputObject;
    }

}
