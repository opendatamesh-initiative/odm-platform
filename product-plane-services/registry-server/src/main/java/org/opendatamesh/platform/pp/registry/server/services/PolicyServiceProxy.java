package org.opendatamesh.platform.pp.registry.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.policy.api.mappers.EventTypeMapper;
import org.opendatamesh.platform.pp.policy.api.mappers.JsonNodeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy {

    private PolicyClient policyClient;
    private final boolean policyServiceActive;

    @Autowired
    EventTypeMapper eventTypeMapper;


    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    public PolicyServiceProxy(
            @Value("${odm.productPlane.policyService.address}") final String serverAddress,
            @Value("${odm.productPlane.policyService.active}") String policyServiceActive
    ) {
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
        try {
            PolicyEvaluationRequestResource evaluationRequest = buildEvaluationRequest(mostRecentDataProduct, newDataProductVersion);
            ValidationResponseResource evaluationResult = policyClient.validateInputObject(evaluationRequest);

            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed during DataProduct version creation");
            }

            return evaluationResult.getResult();
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occured while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }

    private PolicyEvaluationRequestResource buildEvaluationRequest(DataProductVersionDPDS mostRecentDataProduct, DataProductVersionDPDS newDataProductVersion) throws JsonProcessingException {
        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT);
        evaluationRequest.setAfterState(JsonNodeMapper.toJsonNode(eventTypeMapper.toResource(newDataProductVersion)));
        if (mostRecentDataProduct == null) {
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_CREATION);
        } else {
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_UPDATE);
            evaluationRequest.setCurrentState(JsonNodeMapper.toJsonNode(eventTypeMapper.toResource(mostRecentDataProduct)));
        }
        return evaluationRequest;

    }

}
