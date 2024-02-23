package org.opendatamesh.platform.pp.registry.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy extends PolicyClient {

    @Value("${odm.productPlane.policyService.active}")
    private String policyServiceActive;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    public PolicyServiceProxy(@Value("${odm.productPlane.policyService.address}") final String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    // TODO return also why is not compliant
    public Boolean validateDataProductVersionCreation(DataProductVersionDPDS dataProductVersion) throws JsonProcessingException {

        if (policyServiceActive.equals("false")) {
            logger.debug("Skipping policy service");
            return true;
        }

        // Results placeholder
        Boolean answer = false;

        // EVENT creation
        // TODO: check if is an UPDATE (new version of an existing product) or a NEW ENTITY to set beforeState and afterState
        EventResource eventResource = new EventResource(
                EventType.DATA_PRODUCT_VERSION_CREATED,
                dataProductVersion.getInfo().getDataProductId(),
                null, // BEFORE STATE
                dataProductVersion.toEventString() // AFTER STATE
        );

        try {
            // Pass the EVENT (still to define it) to PolicyService for validation --> validatePolicy(eventResource)
            ResponseEntity<PolicyEvaluationResultResource> responseEntity = validatePolicies(eventResource);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Handle validation results
                answer = true; // TODO: result processing
            } else {
                logger.error("There was an error when communicating with Policy service");
                throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while comunicating with the PolicyService: " + responseEntity.getBody());
            }
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while comunicating with the PolicyService: " + e.getMessage());
        }
        return answer;
    }

}
