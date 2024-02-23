package org.opendatamesh.platform.pp.devops.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

public class PolicyServiceProxy extends PolicyClient {

    @Value("${odm.productPlane.policyService.active}")
    private String policyServiceActive;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    public PolicyServiceProxy(@Value("${odm.productPlane.policyService.address}") final String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
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
