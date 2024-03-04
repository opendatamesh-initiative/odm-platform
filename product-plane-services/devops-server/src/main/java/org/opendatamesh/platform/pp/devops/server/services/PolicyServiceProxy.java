package org.opendatamesh.platform.pp.devops.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.TaskResultResource;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceProxy {

    private PolicyClient policyClient;
    private final boolean policyServiceActive;
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceProxy.class);

    @Autowired
    public PolicyServiceProxy(DevOpsConfigurations configurations) {
        this.objectMapper = new ObjectMapper();
        if (configurations.getProductPlane().getPolicyService().getActive()) {
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
        if (!policyServiceActive) return true;
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.ACTIVITY_STAGE_TRANSITION);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.ACTIVITY);
            if (lastExecutedActivity != null) {
                evaluationRequest.setCurrentState(objectMapper.writeValueAsString(lastExecutedActivity));
            }
            evaluationRequest.setAfterState(objectMapper.writeValueAsString(activityToBeExecuted));

            PolicyEvaluationResultResource evaluationResult = policyClient.validateObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("");//TODO
            }
            return evaluationResult.getResult();
        } catch (JsonProcessingException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    public boolean isCallbackResultValid(TaskResultResource taskResult) {
        if (!policyServiceActive) return true;
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.TASK_EXECUTOR_INITIAL_CALL);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.TASK_RESULT);
            evaluationRequest.setCurrentState(objectMapper.writeValueAsString(taskResult));

            PolicyEvaluationResultResource evaluationResult = policyClient.validateObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("");//TODO
            }
            return evaluationResult.getResult();
        } catch (JsonProcessingException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    public boolean isContextuallyCoherent(ActivityResource activity) {
        if (!policyServiceActive) return true;
        try {
            PolicyEvaluationRequestResource evaluationRequest = new PolicyEvaluationRequestResource();
            evaluationRequest.setEvent(PolicyEvaluationRequestResource.EventType.TASK_EXECUTOR_FINAL_CALL);
            evaluationRequest.setResourceType(PolicyEvaluationRequestResource.ResourceType.ACTIVITY);
            evaluationRequest.setCurrentState(objectMapper.writeValueAsString(activity));

            PolicyEvaluationResultResource evaluationResult = policyClient.validateObject(evaluationRequest);
            if (Boolean.FALSE.equals(evaluationResult.getResult())) {
                logger.warn("");//TODO
            }
            return evaluationResult.getResult();
        } catch (JsonProcessingException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

}
