package org.opendatamesh.platform.pp.devops.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.LifecycleResource;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.devops.server.database.mappers.EventTypeMapper;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyValidationClient;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevopsPolicyServiceProxy {

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

    public boolean isStageTransitionValid(
            LifecycleResource currentLifecycle,
            ActivityResource activityToBeExecuted,
            List<TaskResource> activityTasksToBeExecuted
    ) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }

        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setDataProductId(activityToBeExecuted.getDataProductId());
            evaluationRequest.setDataProductVersion(activityToBeExecuted.getDataProductVersion());
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.ACTIVITY_STAGE_TRANSITION);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.ACTIVITY_TRANSITION);
            if (currentLifecycle != null) {
                evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(
                        eventTypeMapper.toEventResource(currentLifecycle, null, null)
                ));
            }
            evaluationRequest.setAfterState(JsonNodeUtils.toJsonNode(
                    eventTypeMapper.toEventResource(currentLifecycle, activityToBeExecuted, activityTasksToBeExecuted)
            ));
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed during stage transition.");
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


    public boolean isCallbackResultValid(TaskResource taskResource) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.TASK_EXECUTION_RESULT);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.TASK_RESULT);
            evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(
                    eventTypeMapper.toEventResource(null, taskResource)
            ));
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed on callback result validation.");
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

    public boolean isContextuallyCoherent(ActivityResource activityResource, DataProductVersionDPDS dataProductVersionDPDS) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setDataProductId(activityResource.getDataProductId());
            evaluationRequest.setDataProductVersion(activityResource.getDataProductVersion());
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.ACTIVITY_EXECUTION_RESULT);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.ACTIVITY_RESULT);
            evaluationRequest.setCurrentState(JsonNodeUtils.toJsonNode(
                    eventTypeMapper.toEventResource(activityResource, dataProductVersionDPDS)
            ));
            ValidationResponseResource evaluationResult = policyValidationClient.validateInputObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed during context coherence validation.");
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

}
