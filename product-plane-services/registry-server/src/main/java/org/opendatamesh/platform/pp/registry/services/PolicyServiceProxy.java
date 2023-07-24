package org.opendatamesh.platform.pp.registry.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.exceptions.BadGatewayException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.PolicyName;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.PolicyValidationRequest;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.PolicyValidationResponse;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.ValidatedPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PolicyServiceProxy {

    
    @Value("${policyserviceaddress}")
    private String policyserviceaddress;

 
    @Value("${skippolicyservice}")
    private String skippolicyservice;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);


    public PolicyServiceProxy() {}

    // TODO return also why is not compliant
    public Boolean validateDataProductVersion(DataProductVersion dataProductVersion, PolicyName policyName) {

        if (skippolicyservice.equals("true")) {
            logger.debug("Skipping policy service");
            return true;
        }

        Map<String, Object> requestBody = objectMapper.convertValue(dataProductVersion, Map.class);

        try {
            PolicyValidationRequest request = new PolicyValidationRequest(requestBody);
            ResponseEntity<PolicyValidationResponse> responseEntity = restTemplate.postForEntity(
                    policyserviceaddress + "/api/v1/planes/utility/policy-services/opa/validate?id=" + policyName,
                    request, PolicyValidationResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                PolicyValidationResponse policyValidationResponse = responseEntity.getBody();
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
                    "An error occurred while comunicating with the policyService");
        }
    }
}
