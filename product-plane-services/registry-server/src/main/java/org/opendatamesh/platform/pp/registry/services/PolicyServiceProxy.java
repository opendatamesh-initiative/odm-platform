package org.opendatamesh.platform.pp.registry.services;

import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.exceptions.BadGatewayException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.PolicyName;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.PolicyValidationResponse;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.ValidatedPolicy;
import org.opendatamesh.platform.up.policy.api.v1.clients.PolicyServiceClient;
import org.opendatamesh.platform.up.policy.api.v1.resources.ValidateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy extends PolicyServiceClient {

    @Value("${skippolicyservice}")
    private String skippolicyservice;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    public PolicyServiceProxy(@Value("${policyserviceaddress}") final String serverAddress) {
        super(serverAddress);
    }

    // TODO return also why is not compliant
    public Boolean validateDataProductVersion(DataProductVersion dataProductVersion, PolicyName policyName) {

        if (skippolicyservice.equals("true")) {
            logger.debug("Skipping policy service");
            return true;
        }

        try {
            ResponseEntity<ValidateResponse> responseEntity = validateDocumentByPoliciesIds(
                    dataProductVersion,
                    policyName.toString()
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                PolicyValidationResponse policyValidationResponse = mapper.convertValue(
                        responseEntity.getBody(),
                        PolicyValidationResponse.class
                );
                for (ValidatedPolicy p : policyValidationResponse.getValidatedPolicyList()) {
                    if (p.getPolicy().equals(policyName)) {
                        return p.getValidationResult().getResult().getAllow();
                    }
                }
                logger.error("The policy required to validate the request wasn't found on Provision service");
                throw new RuntimeException(
                        "The policy required to validate the request wasn't found on Provision service");
            } else {
                logger.error("There was an error when communicating with Policy service");
                throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_01_POLICY_SERVICE_ERROR,
                    "An error occurred while comunicating with the policyService");
            }
        } catch (Exception e) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_01_POLICY_SERVICE_ERROR,
                    "An error occurred while comunicating with the policyService: " + e.getMessage());
        }
    }
}
