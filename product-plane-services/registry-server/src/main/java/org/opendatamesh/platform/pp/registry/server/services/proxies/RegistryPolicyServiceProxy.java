package org.opendatamesh.platform.pp.registry.server.services.proxies;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyValidationClient;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
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

    public boolean validateDataProductVersion(DataProductVersionDPDS oldDpds, DataProductVersionDPDS newDpds) {
        if (!this.policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
            evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(newDpds)));
            evaluationRequest.setDataProductId(newDpds.getInfo().getDataProductId());
            evaluationRequest.setDataProductVersion(newDpds.getInfo().getVersionNumber());
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_VERSION_CREATION);
            if (oldDpds != null) {
                evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(oldDpds)));
            }
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

    public boolean isCompliantWithPolicies(DataProductResource dataProduct) {
        if (!this.policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
            DataProductVersionDPDS dpdsHead = descriptorFromDataProduct(dataProduct);

            evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(dpdsHead)));
            evaluationRequest.setDataProductId(dataProduct.getId());
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_CREATION);
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);

            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed during DataProduct creation");
            }

            return evaluationResult.getResult();
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while invoking policy service to validate data product: " + e.getMessage(),
                    e
            );
        }
    }

    public boolean isCompliantWithPolicies(DataProductResource oldDataProduct, DataProductResource newDataProduct) {
        if (!this.policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
            DataProductVersionDPDS oldDpdsHead = descriptorFromDataProduct(oldDataProduct);
            DataProductVersionDPDS newDpdsHead = descriptorFromDataProduct(newDataProduct);

            evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(oldDpdsHead)));
            evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(newDpdsHead)));
            evaluationRequest.setDataProductId(oldDataProduct.getId());
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_UPDATE);
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);

            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed during DataProduct update");
            }

            return evaluationResult.getResult();
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while invoking policy service to validate data product: " + e.getMessage(),
                    e
            );
        }
    }

    private DataProductVersionDPDS descriptorFromDataProduct(DataProductResource dataProduct) {
        DataProductVersionDPDS dpdsHead = new DataProductVersionDPDS();
        InfoDPDS dpdsHeadInfo = new InfoDPDS();
        dpdsHeadInfo.setDomain(dataProduct.getDomain());
        dpdsHeadInfo.setFullyQualifiedName(dataProduct.getFullyQualifiedName());
        dpdsHeadInfo.setDescription(dataProduct.getDescription());
        dpdsHead.setInfo(dpdsHeadInfo);
        return dpdsHead;
    }
}
