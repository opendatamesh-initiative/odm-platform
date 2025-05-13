package org.opendatamesh.platform.pp.policy.server.resources.notificationservice.eventstates;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;

public class PolicyNotificationEventState {
    private PolicyResource policy;

    public PolicyNotificationEventState() {
    }

    public PolicyResource getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyResource policy) {
        this.policy = policy;
    }

    public JsonNode toJsonNode() {
        return new ObjectMapper().valueToTree(this);
    }
}
