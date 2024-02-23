package org.opendatamesh.platform.pp.devops.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class PolicyServiceProxy extends PolicyClient {

    private Boolean policyServiceActive;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    @Autowired
    public PolicyServiceProxy(DevOpsConfigurations configurations) {
        super(configurations.getProductPlane().getPolicyService().getAddress(), ObjectMapperFactory.JSON_MAPPER);
        this.policyServiceActive = configurations.getProductPlane().getPolicyService().getActive();
    }

    // ===============================================================================
    // VALIDATE Stage Transitions
    // ===============================================================================

    public boolean validateStageTransition() {
        // TODO

        if (!policyServiceActive) {
            logger.debug("Skipping policy service");
            return true;
        }

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

        if (!policyServiceActive) {
            logger.debug("Skipping policy service");
            return true;
        }

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

        if (!policyServiceActive) {
            logger.debug("Skipping policy service");
            return true;
        }

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
        try {
            // 2. Invoke the PolicyService
            ResponseEntity<PolicyResultResource> policyResponse = validatePolicy(); // Pass the event //TODO
            // 3. Process results
            if(policyResponse.getStatusCode().is2xxSuccessful()) {
                // TODO - handle results
                return true;
            } else {
                throw new BadGatewayException(
                        ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                        "An error occurred while comunicating with the PolicyService: " + policyResponse.getBody()
                );
            }
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while comunicating with the PolicyService: " + e.getMessage()
            );
        }
    }

}
