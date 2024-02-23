package org.opendatamesh.platform.pp.registry.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResultResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy extends PolicyClient {

    @Value("${odm.utilityPlane.policyServices.open-policy-agent.active}")
    private String policyServiceActive;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    public PolicyServiceProxy(@Value("${odm.utilityPlane.policyServices.open-policy-agent.address}") final String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    // TODO return also why is not compliant
    public Boolean validateDataProductVersion(DataProductVersion dataProductVersion) {

        if (policyServiceActive.equals("false")) {
            logger.debug("Skipping policy service");
            return true;
        }

        // Results placeholder
        Boolean answer = false;

        try {
            // Pass the EVENT (still to define it) to PolicyService for validation
            ResponseEntity<PolicyResultResource> responseEntity = validatePolicy();
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
