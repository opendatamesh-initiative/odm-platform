package org.opendatamesh.platform.pp.registry.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy {

    private PolicyClient policyClient;
    private final boolean policyServiceActive;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    public PolicyServiceProxy(
            @Value("${odm.productPlane.policyService.address}") final String serverAddress,
            @Value("${odm.productPlane.policyService.active}") String policyServiceActive
    ) {
        objectMapper = new ObjectMapper();
        if ("true".equals(policyServiceActive)) {
            this.policyClient = new PolicyClientImpl(
                    serverAddress,
                    ObjectMapperFactory.JSON_MAPPER
            );
            this.policyServiceActive = true;
        } else {
            this.policyServiceActive = false;
        }
    }

    public boolean validateDataProductVersionCreation(DataProductVersionDPDS mostRecentDataProduct, DataProductVersionDPDS newDataProductVersion) {
        if (!this.policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }

        PolicyEvaluationRequestResource evaluationRequest = buildEvaluationRequest(mostRecentDataProduct, newDataProductVersion);
        PolicyEvaluationResultResource evaluationResult = policyClient.validateObject(evaluationRequest);

        if (Boolean.FALSE.equals(evaluationResult.getResult())) {
            //TODO prettify the log of failed policies
            logger.warn(evaluationResult.getOutputObject());
        }

        return evaluationResult.getResult();
    }

    private PolicyEvaluationRequestResource buildEvaluationRequest(DataProductVersionDPDS mostRecentDataProduct, DataProductVersionDPDS newDataProductVersion) {
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT);
            evaluationRequest.setAfterState(objectMapper.writeValueAsString(newDataProductVersion));
            if (mostRecentDataProduct == null) {
                evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_CREATION);
            } else {
                evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_UPDATE);
                evaluationRequest.setCurrentState(objectMapper.writeValueAsString(mostRecentDataProduct));
            }
            return evaluationRequest;
        } catch (JsonProcessingException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }
}
