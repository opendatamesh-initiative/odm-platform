package org.opendatamesh.platform.pp.devops.server.services;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy {

    private PolicyClient policyClient;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    @Autowired
    public PolicyServiceProxy(DevOpsConfigurations configurations) {
        if (configurations.getProductPlane().getPolicyService().getActive()) {
            this.policyClient = new PolicyClientImpl(
                    configurations.getProductPlane().getPolicyService().getAddress(),
                    ObjectMapperFactory.JSON_MAPPER
            );
        } else {
            //TODO
        }
    }

    // ===============================================================================
    // VALIDATE Stage Transitions
    // ===============================================================================

    public boolean validateStageTransition() {
        // TODO

        // Results placeholder
        Boolean answer = false;

        // 1. Create the EVENT
        EventResource eventResource = new EventResource(); // TODO
        // Fill this event with the relevant information for this policy check

        // 2. Validate the EVENT
        answer = getPolicyValidationResult(eventResource);

        return answer;
    }


    // ===============================================================================
    // VALIDATE Callback Results
    // ===============================================================================

    public Boolean validateCallbackResult() {
        // TODO

        // Results placeholder
        Boolean answer = false;

        // 1. Create the EVENT
        EventResource eventResource = new EventResource(); // TODO
        // Fill this event with the relevant information for this policy check

        // 2. Validate the EVENT
        answer = getPolicyValidationResult(eventResource);

        return answer;
    }


    // ===============================================================================
    // VALIDATE Contextual Coherence
    // ===============================================================================

    public Boolean validateContextualCoherence() {
        // TODO

        // Results placeholder
        Boolean answer = false;

        // 1. Create the EVENT
        EventResource eventResource = new EventResource(); // TODO
        // Fill this event with the relevant information for this policy check

        // 2. Validate the EVENT
        answer = getPolicyValidationResult(eventResource);

        return answer;
    }


    // ===============================================================================
    // UTILS
    // ===============================================================================

    private Boolean getPolicyValidationResult(EventResource eventResource) {
        PolicyEvaluationResultResource result = policyClient.validateObject(eventResource);
        return result.getResult();
    }

}
