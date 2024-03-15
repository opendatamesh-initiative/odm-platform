package org.opendatamesh.platform.pp.devops.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.TaskResultResource;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy {

    private PolicyClient policyClient;
    private final boolean policyServiceActive;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    @Autowired
    public PolicyServiceProxy(DevOpsConfigurations configurations) {
        this.objectMapper = new ObjectMapper();
        if (Boolean.TRUE.equals(configurations.getProductPlane().getPolicyService().getActive())) {
            this.policyClient = new PolicyClientImpl(
                    configurations.getProductPlane().getPolicyService().getAddress(),
                    ObjectMapperFactory.JSON_MAPPER
            );
            this.policyServiceActive = true;
        } else {
            this.policyServiceActive = false;
        }
    }

    public boolean isStageTransitionValid(ActivityResource lastExecutedActivity, ActivityResource activityToBeExecuted) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }

        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.ACTIVITY_STAGE_TRANSITION);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.ACTIVITY);
            if (lastExecutedActivity != null) {
                evaluationRequest.setCurrentState(objectMapper.writeValueAsString(lastExecutedActivity));
            }
            evaluationRequest.setAfterState(objectMapper.writeValueAsString(activityToBeExecuted));

            ValidationResponseResource evaluationResult = policyClient.validateInputObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed during stage transition.");
            }
            return evaluationResult.getResult();
        } catch (JsonProcessingException e) {
            throw new InternalServerException(e); //TODO
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occured while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }

    public boolean isCallbackResultValid(TaskResultResource taskResult) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.TASK_EXECUTOR_INITIAL_CALL);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.TASK_RESULT);
            evaluationRequest.setCurrentState(objectMapper.writeValueAsString(taskResult));

            ValidationResponseResource evaluationResult = policyClient.validateInputObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed on callback result validation.");
            }
            return evaluationResult.getResult();
        } catch (JsonProcessingException e) {
            throw new InternalServerException(e); //TODO
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occured while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }

    public boolean isContextuallyCoherent(ActivityResource activity) {
        if (!policyServiceActive) {
            logger.info("Policy Service is not active;");
            return true;
        }
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.TASK_EXECUTOR_FINAL_CALL);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.ACTIVITY);
            evaluationRequest.setCurrentState(objectMapper.writeValueAsString(activity));

            ValidationResponseResource evaluationResult = policyClient.validateInputObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("Policy evaluation failed during context coherence validation.");
            }
            return evaluationResult.getResult();
        } catch (JsonProcessingException e) {
            throw new InternalServerException(e); //TODO
        } catch (Exception e) {
            throw new BadGatewayException(
                    ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occured while invoking policy service to validate data product version: " + e.getMessage(),
                    e
            );
        }
    }

}
