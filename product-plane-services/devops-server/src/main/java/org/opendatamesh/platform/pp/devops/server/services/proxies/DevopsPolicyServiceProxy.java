package org.opendatamesh.platform.pp.devops.server.services.proxies;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.LifecycleResource;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.devops.server.database.mappers.EventTypeMapper;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyValidationClient;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DevopsPolicyServiceProxy {

    /**
     * Result class to hold policy validation results with detailed error information
     */
    public static class PolicyValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public PolicyValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private PolicyValidationClient policyValidationClient;
    private final boolean policyServiceActive;

    @Autowired
    EventTypeMapper eventTypeMapper;

    private static final Logger logger = LoggerFactory.getLogger(DevopsPolicyServiceProxy.class);

    @Autowired
    public DevopsPolicyServiceProxy(DevOpsConfigurations configurations) {
        if (Boolean.TRUE.equals(configurations.getProductPlane().getPolicyService().getActive())) {
            this.policyValidationClient = new PolicyClientImpl(
                    configurations.getProductPlane().getPolicyService().getAddress(),
                    ObjectMapperFactory.JSON_MAPPER
            );
            this.policyServiceActive = true;
        } else {
            this.policyServiceActive = false;
        }
    }

    public PolicyValidationResult isStageTransitionValid(
            LifecycleResource currentLifecycle,
            ActivityResource activityToBeExecuted,
            List<TaskResource> activityTasksToBeExecuted
    ) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return new PolicyValidationResult(true, null);
        }

        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setDataProductId(activityToBeExecuted.getDataProductId());
            evaluationRequest.setDataProductVersion(activityToBeExecuted.getDataProductVersion());
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.ACTIVITY_STAGE_TRANSITION);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.ACTIVITY_STAGE_TRANSITION);
            if (currentLifecycle != null) {
                evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(
                        eventTypeMapper.toEventResource(currentLifecycle, null, null)
                ));
            }
            evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(
                    eventTypeMapper.toEventResource(currentLifecycle, activityToBeExecuted, activityTasksToBeExecuted)
            ));
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);
            return checkPoliciesValidationResults(evaluationResult, "stage transition");
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }


    public PolicyValidationResult isCallbackResultValid(TaskResource taskResource) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return new PolicyValidationResult(true, null);
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.TASK_EXECUTION_RESULT);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.TASK_EXECUTION_RESULT);
            evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(
                    eventTypeMapper.toEventResource(null, taskResource)
            ));
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);
            return checkPoliciesValidationResults(evaluationResult, "callback result validation");
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }

    public PolicyValidationResult isContextuallyCoherent(ActivityResource activityResource, DataProductVersionDPDS dataProductVersionDPDS) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return new PolicyValidationResult(true, null);
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setDataProductId(activityResource.getDataProductId());
            evaluationRequest.setDataProductVersion(activityResource.getDataProductVersion());
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.ACTIVITY_EXECUTION_RESULT);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.ACTIVITY_EXECUTION_RESULT);
            evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(
                    eventTypeMapper.toEventResource(activityResource, dataProductVersionDPDS)
            ));
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);
            return checkPoliciesValidationResults(evaluationResult, "context coherence validation");
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occurred while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * This method performs the check, returning false only if blocking policies fail.
     * Non-blocking policy failures are logged but don't block the operation.
     */
    private PolicyValidationResult checkPoliciesValidationResults(ValidationResponseResource validationResults, String operation) {
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
        if (StringUtils.hasText(allFailedPoliciesIds)) {
            logger.warn("Policy evaluation failed during {}: {}", operation, allFailedPoliciesIds);
        }
        // Only return false if there are failed blocking policies
        boolean isValid = !StringUtils.hasText(failedBlockingPolicies);
        String errorMessage = StringUtils.hasText(failedBlockingPolicies) ? 
            "Blocking policies failed: " + failedBlockingPolicies : null;
        return new PolicyValidationResult(isValid, errorMessage);
    }

    private String getPolicyIdentifier(PolicyEvaluationResultResource policyEvaluationResultResource) {
        return String.format("{name: %s, rootId: %s, id: %s}",
                policyEvaluationResultResource.getPolicy().getName(),
                policyEvaluationResultResource.getPolicy().getRootId().toString(),
                policyEvaluationResultResource.getPolicy().getId().toString());
    }

}
