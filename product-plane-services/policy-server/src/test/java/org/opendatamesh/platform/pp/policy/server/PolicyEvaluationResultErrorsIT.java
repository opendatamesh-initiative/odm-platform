package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyEvaluationResultErrorsIT extends ODMPolicyIT {

    // ======================================================================================
    // CREATE PolicyEvaluationResult
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyEvaluationResultError400xx() throws JsonProcessingException {

        // 40001 - Empty PolicyEvaluationResult
        ResponseEntity<ObjectNode> postResponse = policyClient.createPolicyEvaluationResultResponseEntity(null);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.BAD_REQUEST,
                PolicyApiStandardErrors.SC400_03_POLICY_EVALUATION_RESULT_IS_EMPTY,
                "PolicyEvaluationResult object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyEvaluationResultError404xx() throws JsonProcessingException {

        // Resources
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResultResource(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1
        );

        // 40401 - Policy not found (parent policy not found)
        policyEvaluationResultResource.setPolicyId(3L);
        ResponseEntity<ObjectNode> postResponse = policyClient.createPolicyEvaluationResultResponseEntity(policyEvaluationResultResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND,
                "Policy with ID [3] not found"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyEvaluationResultError422xx() throws JsonProcessingException {

        // Resources
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResultResource(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1
        );
        Long policyEvaluationResultPolicyId = policyEvaluationResultResource.getPolicyId();
        Boolean policyEvaluationResultResult = policyEvaluationResultResource.getResult();
        ResponseEntity<ObjectNode> postResponse;

        // 42203 - PolicyEvaluationResult is invalid - PolicyEvaluationResult policyID cannot be null
        policyEvaluationResultResource.setPolicyId(null);
        postResponse = policyClient.createPolicyEvaluationResultResponseEntity(policyEvaluationResultResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                "PolicyEvaluationResult policyID cannot be null"
        );

        // 42203 - PolicyEngine is invalid - PolicyEngine name cannot be null
        policyEvaluationResultResource.setPolicyId(policyEvaluationResultPolicyId);
        policyEvaluationResultResource.setResult(null);
        postResponse = policyClient.createPolicyEvaluationResultResponseEntity(policyEvaluationResultResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                "PolicyEvaluationResult result cannot be null"
        );

        // 42203 - PolicyEvaluationResult is invalid
        // - The policy with policy ID [evaluationResult.getPolicyId()] is inactive. Cannot add a result to an inactive policy
        // Create 2 version of a parent policy
        PolicyEngineResource parentPolicyEngine = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentPolicyEngine.getId());
        PolicyResource updatedParentPolicyResource = createPolicyResource(ODMPolicyResources.RESOURCE_POLICY_1_UPDATED);
        updatedParentPolicyResource.setPolicyEngine(parentPolicyEngine);
        updatedParentPolicyResource.setRootId(parentPolicyResource.getRootId());
        updatedParentPolicyResource.setCreatedAt(parentPolicyResource.getCreatedAt());
        policyClient.updatePolicyResponseEntity(parentPolicyResource.getRootId(), updatedParentPolicyResource);
        policyEvaluationResultResource.setPolicyId(parentPolicyResource.getId());
        policyEvaluationResultResource.setResult(policyEvaluationResultResult);
        postResponse = policyClient.createPolicyEvaluationResultResponseEntity(policyEvaluationResultResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                "The policy with ID [" + policyEvaluationResultResource.getPolicyId() + "] is inactive. "
                        + "Cannot add a result to an inactive policy"
        );

    }

    // ======================================================================================
    // UPDATE PolicyEvaluationResult
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEvaluationResultError400xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1,
                parentPolicyResource.getId()
        );

        // 40001 - Empty PolicyEvaluationResult
        ResponseEntity<ObjectNode> putResponse = policyClient.updatePolicyEvaluationResultResponseEntity(
                policyEvaluationResultResource.getId(), null
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.BAD_REQUEST,
                PolicyApiStandardErrors.SC400_03_POLICY_EVALUATION_RESULT_IS_EMPTY,
                "PolicyEvaluationResult object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEvaluationResultError404xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1,
                parentPolicyResource.getId()
        );

        // 40403 - PolicyEvaluationResult not found (parent policy evaluation result)
        policyEvaluationResultResource.setPolicyId(parentPolicyResource.getId());
        policyEvaluationResultResource.setPolicy(null);
        ResponseEntity<ObjectNode> putResponse = policyClient.updatePolicyEvaluationResultResponseEntity(
                7L, policyEvaluationResultResource
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND,
                "Resource with ID [7] not found"
        );

        // 40402 - Resource not found (parent policy not found)
        policyEvaluationResultResource.setPolicyId(3L);
        policyEvaluationResultResource.setPolicy(null);
        putResponse = policyClient.updatePolicyEvaluationResultResponseEntity(
                policyEvaluationResultResource.getId(), policyEvaluationResultResource
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND,
                "Policy with ID [3] not found"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEvaluationResultError422xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1,
                parentPolicyResource.getId()
        );
        Long policyEvaluationResultPolicyId = policyEvaluationResultResource.getPolicyId();
        Boolean policyEvaluationResultResult = policyEvaluationResultResource.getResult();
        ResponseEntity<ObjectNode> putResponse;

        // 42203 - PolicyEvaluationResult is invalid - PolicyEvaluationResult policyID cannot be null
        policyEvaluationResultResource.setPolicyId(null);
        policyEvaluationResultResource.setPolicy(null);
        putResponse = policyClient.updatePolicyEvaluationResultResponseEntity(
                policyEvaluationResultResource.getId(), policyEvaluationResultResource
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                "PolicyEvaluationResult policyID cannot be null"
        );

        // 42203 - PolicyEngine is invalid - PolicyEngine name cannot be null
        policyEvaluationResultResource.setPolicyId(policyEvaluationResultPolicyId);
        policyEvaluationResultResource.setPolicy(null);
        policyEvaluationResultResource.setResult(null);
        putResponse = policyClient.updatePolicyEvaluationResultResponseEntity(
                policyEvaluationResultResource.getId(), policyEvaluationResultResource
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                "PolicyEvaluationResult result cannot be null"
        );

        // 42203 - PolicyEvaluationResult is invalid
        // - The policy with policy ID [evaluationResult.getPolicyId()] is inactive. Cannot add a result to an inactive policy
        // Create 2 version of a parent policy
        PolicyResource updatedParentPolicyResource = createPolicyResource(ODMPolicyResources.RESOURCE_POLICY_1_UPDATED);
        updatedParentPolicyResource.setPolicyEngine(parentEngineResource);
        updatedParentPolicyResource.setRootId(parentPolicyResource.getRootId());
        updatedParentPolicyResource.setCreatedAt(parentPolicyResource.getCreatedAt());
        policyClient.updatePolicyResponseEntity(parentPolicyResource.getRootId(), updatedParentPolicyResource);
        policyEvaluationResultResource.setPolicyId(parentPolicyResource.getId());
        policyEvaluationResultResource.setPolicy(null);
        policyEvaluationResultResource.setResult(policyEvaluationResultResult);
        putResponse = policyClient.updatePolicyEvaluationResultResponseEntity(
                policyEvaluationResultResource.getId(), policyEvaluationResultResource
        );
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                "The policy with ID [" + policyEvaluationResultResource.getPolicyId() + "] is inactive. "
                        + "Cannot add a result to an inactive policy"
        );

    }

    // ======================================================================================
    // READ ALL PolicyEvaluationResults
    // ======================================================================================

    // No specific errors to test excluding 500

    // ======================================================================================
    // READ ONE PolicyEvaluationResult
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicyEvaluationResultError404xx() throws JsonProcessingException {

        // 40403 - PolicyEvaluationResult not found
        ResponseEntity getResponse = policyClient.readOnePolicyEvaluationResultResponseEntity(2L);
        verifyResponseErrorObjectNode(
                getResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND,
                "Resource with ID [2] not found"
        );

    }

    // ======================================================================================
    // DELETE PolicyEvaluationResult
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteOnePolicyEvaluationResultError404xx() throws JsonProcessingException {

        // 40401 - Resource not found
        ResponseEntity deleteResponse = policyClient.deletePolicyEvaluationResultResponseEntity(2L);
        verifyResponseErrorObjectNode(
                deleteResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND,
                "Resource with ID [2] not found"
        );

    }

}
