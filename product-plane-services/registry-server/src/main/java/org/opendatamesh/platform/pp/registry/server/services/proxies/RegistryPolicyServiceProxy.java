package org.opendatamesh.platform.pp.registry.server.services.proxies;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyValidationClient;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.registry.server.database.mappers.EventTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegistryPolicyServiceProxy {

    private PolicyValidationClient policyValidationClient;
    private final boolean policyServiceActive;

    @Autowired
    private EventTypeMapper eventTypeMapper;

    private static final Logger logger = LoggerFactory.getLogger(RegistryPolicyServiceProxy.class);

    public RegistryPolicyServiceProxy(
            @Value("${odm.productPlane.policyService.address}") final String serverAddress,
            @Value("${odm.productPlane.policyService.active}") String policyServiceActive
    ) {
        if ("true".equals(policyServiceActive)) {
            this.policyValidationClient = new PolicyClientImpl(
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
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);

            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed during DataProduct version creation");
            }

            return evaluationResult.getResult();
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }

    private PolicyEvaluationRequestResource buildEvaluationRequest(DataProductVersionDPDS mostRecentDataProduct, DataProductVersionDPDS newDataProductVersion) throws JsonProcessingException {
        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT);
        evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(newDataProductVersion)));
        evaluationRequest.setDataProductId(newDataProductVersion.getInfo().getDataProductId());
        evaluationRequest.setDataProductVersion(newDataProductVersion.getInfo().getVersionNumber());
        if (mostRecentDataProduct == null) {
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_CREATION);
        } else {
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_UPDATE);
            evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(mostRecentDataProduct)));
        }
        return evaluationRequest;

    }

}
