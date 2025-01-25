package org.opendatamesh.platform.pp.registry.server.services.proxies;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyValidationClient;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.mappers.EventTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public void validateDataProductVersion(DataProductVersionDPDS oldDpds, DataProductVersionDPDS newDpds) {
        if (!this.policyServiceActive) {
            logger.info("Policy Service is not active;");
            return;
        }

        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
        evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(newDpds)));
        evaluationRequest.setDataProductId(newDpds.getInfo().getDataProductId());
        evaluationRequest.setDataProductVersion(newDpds.getInfo().getVersionNumber());
        evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_VERSION_CREATION);
        if (oldDpds != null) {
            evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(oldDpds)));
        }
        ValidationResponseResource evaluationResult = validateInputObject(evaluationRequest);
        checkPoliciesValidationResults(evaluationResult);
    }

    public void validateDataProduct(DataProductResource dataProduct) {
        if (!this.policyServiceActive) {
            logger.info("Policy Service is not active;");
            return;
        }
        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
        DataProductVersionDPDS dpdsHead = descriptorFromDataProduct(dataProduct);

        evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(dpdsHead)));
        evaluationRequest.setDataProductId(dataProduct.getId());
        evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_CREATION);
        ValidationResponseResource evaluationResult = validateInputObject(evaluationRequest);
        checkPoliciesValidationResults(evaluationResult);
    }

    public void validateDataProduct(DataProductResource oldDataProduct, DataProductResource newDataProduct) {
        if (!this.policyServiceActive) {
            logger.info("Policy Service is not active;");
            return;
        }

        PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
        evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.DATA_PRODUCT_DESCRIPTOR);
        DataProductVersionDPDS oldDpdsHead = descriptorFromDataProduct(oldDataProduct);
        DataProductVersionDPDS newDpdsHead = descriptorFromDataProduct(newDataProduct);

        evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(oldDpdsHead)));
        evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(eventTypeMapper.toEventResource(newDpdsHead)));
        evaluationRequest.setDataProductId(oldDataProduct.getId());
        evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.DATA_PRODUCT_UPDATE);
        ValidationResponseResource evaluationResult = validateInputObject(evaluationRequest);
        checkPoliciesValidationResults(evaluationResult);
    }

    private void checkPoliciesValidationResults(ValidationResponseResource validationResults) {
        String allFailedPoliciesIds = "";
        String failedBlockingPolicies = validationResults.getPolicyResults()
                .stream()
                .filter(policyResult -> Boolean.FALSE.equals(policyResult.getResult()))
                .filter(policyResult -> Boolean.TRUE.equals(policyResult.getPolicy().getBlockingFlag()))
                .map(this::getPolicyIdentifier)
                .reduce("", (first, second) -> StringUtils.hasText(first) ? first + ", " + second : second);
        if (StringUtils.hasText(failedBlockingPolicies)) {
            allFailedPoliciesIds = allFailedPoliciesIds + " Blocking Policies: [ " + failedBlockingPolicies + " ]";
        }
        String failedNonBlockingPolicies = validationResults.getPolicyResults()
                .stream()
                .filter(policyResult -> Boolean.FALSE.equals(policyResult.getResult()))
                .filter(policyResult -> Boolean.FALSE.equals(policyResult.getPolicy().getBlockingFlag()))
                .map(this::getPolicyIdentifier)
                .reduce("", (first, second) -> StringUtils.hasText(first) ? first + ", " + second : second);
        if (StringUtils.hasText(failedNonBlockingPolicies)) {
            allFailedPoliciesIds = allFailedPoliciesIds + " Non-Blocking Policies: [ " + failedNonBlockingPolicies + " ]";
        }
        logger.warn("The data product is not compliant to: {}", allFailedPoliciesIds);
        if (StringUtils.hasText(failedBlockingPolicies)) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_03_DESCRIPTOR_NOT_COMPLIANT,
                    String.format("The data product is not compliant to: %s", allFailedPoliciesIds));
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

    private ValidationResponseResource validateInputObject(PolicyEvaluationRequestResource evaluationRequest) {
        try {
            return policyValidationClient.validateInputObject(evaluationRequest);

        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }

    private String getPolicyIdentifier(PolicyEvaluationResultResource policyEvaluationResultResource) {
        return String.format("{name: %s, rootId: %s, id: %s}", policyEvaluationResultResource.getPolicy().getName(), policyEvaluationResultResource.getPolicy().getRootId().toString(), policyEvaluationResultResource.getPolicy().getId().toString());
    }
}
