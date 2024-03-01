package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyErrorsIT extends ODMPolicyIT {

    // ======================================================================================
    // CREATE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyError400xx() throws JsonProcessingException {

        // 40001 - Empty Policy
        ResponseEntity<ErrorRes> postResponse = policyClient.createPolicy(null);
        verifyResponseError(
                postResponse,
                HttpStatus.BAD_REQUEST,
                PolicyApiStandardErrors.SC400_02_POLICY_IS_EMPTY,
                "Policy object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyError422xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource policyResource = createPolicyResource(ODMPolicyResources.RESOURCE_POLICY_1);
        policyResource.setPolicyEngineId(parentEngineResource.getId());
        String policyName = policyResource.getName();
        Long policyPolicyEngineId = policyResource.getPolicyEngineId();
        ResponseEntity<ErrorRes> postResponse;

        // 42201 - Policy is invalid - Policy name cannot be null
        policyResource.setName(null);
        postResponse = policyClient.createPolicy(policyResource);
        verifyResponseError(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                "Policy name cannot be null"
        );

        // 42201 - Policy is invalid - Policy policyEngineId cannot be null
        policyResource.setName(policyName);
        policyResource.setPolicyEngineId(null);
        postResponse = policyClient.createPolicy(policyResource);
        verifyResponseError(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                "Policy policyEngineId or PolicyEngine object cannot be null"
        );

        // 42205 - Policy is invalid - Policy with name [" + policy.getName() + "] already exists
        policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1, policyPolicyEngineId);
        policyResource.setId(null);
        postResponse = policyClient.createPolicy(policyResource);
        verifyResponseError(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_04_POLICY_ALREADY_EXISTS,
                "Policy with name [" + policyResource.getName() + "] already exists"
        );

    }

    // ======================================================================================
    // UPDATE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyError400xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());

        // 40001 - Empty Policy
        ResponseEntity<ErrorRes> putResponse = policyClient.updatePolicy(
                parentPolicyResource.getId(), null
        );
        verifyResponseError(
                putResponse,
                HttpStatus.BAD_REQUEST,
                PolicyApiStandardErrors.SC400_02_POLICY_IS_EMPTY,
                "Policy object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyError404xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyResource updatedPolicyResource = createPolicyResource(ODMPolicyResources.RESOURCE_POLICY_1_UPDATED);

        // 40401 - Resource not found (parent policy)
        ResponseEntity<ErrorRes> putResponse = policyClient.updatePolicy(
                7L, updatedPolicyResource
        );
        verifyResponseError(
                putResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND,
                "Resource with id [7] not found"
        );

        // 40401 - Resource not found (parent policy engine not found)
        updatedPolicyResource.setRootId(parentPolicyResource.getRootId());
        updatedPolicyResource.setPolicyEngineId(3L);
        putResponse = policyClient.updatePolicy(parentPolicyResource.getId(), updatedPolicyResource);
        verifyResponseError(
                putResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND,
                "Resource with id [3] not found"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyError422xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyResource policyResource = createPolicyResource(ODMPolicyResources.RESOURCE_POLICY_1_UPDATED);
        policyResource.setPolicyEngineId(parentEngineResource.getId());
        String policyName = policyResource.getName();
        Long policyPolicyEngineId = policyResource.getPolicyEngineId();
        ResponseEntity<ErrorRes> putResponse;

        // 42201 - Policy is invalid - Policy name cannot be null
        policyResource.setName(null);
        putResponse = policyClient.updatePolicy(
                parentPolicyResource.getId(), policyResource
        );
        verifyResponseError(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                "Policy name cannot be null"
        );

        // 42201 - Policy is invalid - Policy policyEngineId cannot be null
        policyResource.setName(policyName);
        policyResource.setPolicyEngineId(null);
        putResponse = policyClient.updatePolicy(
                parentPolicyResource.getId(), policyResource
        );
        verifyResponseError(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                "Policy policyEngineId or PolicyEngine object cannot be null"
        );

        // 42205 - Policy is invalid - Policy with name [" + policy.getName() + "] already exists with a differet rootID
        policyResource.setPolicyEngineId(policyPolicyEngineId);
        policyResource.setRootId(7L);
        policyResource.setCreatedAt(parentPolicyResource.getCreatedAt());
        putResponse = policyClient.updatePolicy(
                parentPolicyResource.getRootId(), policyResource
        );
        verifyResponseError(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_04_POLICY_ALREADY_EXISTS,
                "Policy with name [" + policyResource.getName() + "] already exists with a differet rootID"
        );

    }

    // ======================================================================================
    // READ ALL Policies
    // ======================================================================================

    // No specific errors to test excluding 500

    // ======================================================================================
    // READ ONE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicyError404xx() throws JsonProcessingException {

        // 40401 - Resource not found
        ResponseEntity getResponse = policyClient.readOnePolicy(2L);
        verifyResponseError(
                getResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND
        );

    }

    // ======================================================================================
    // DELETE Policy
    // ======================================================================================
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteOnePolicyError404xx() throws JsonProcessingException {

        // 40401 - Resource not found
        ResponseEntity deleteResponse = policyClient.deletePolicy(2L);
        verifyResponseError(
                deleteResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND
        );

    }


}
