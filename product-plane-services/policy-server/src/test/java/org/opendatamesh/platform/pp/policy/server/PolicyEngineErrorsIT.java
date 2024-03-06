package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyEngineErrorsIT extends ODMPolicyIT {

    // ======================================================================================
    // CREATE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyEngineError400xx() throws JsonProcessingException {

        // 40001 - Empty PolicyEngine
        ResponseEntity<ErrorRes> postResponse = policyClient.createPolicyEngineResponseEntity(null);
        verifyResponseError(
                postResponse,
                HttpStatus.BAD_REQUEST,
                PolicyApiStandardErrors.SC400_01_POLICY_ENGINE_IS_EMPTY,
                "PolicyEngine object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyEngineError422xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource policyEngineResource = createPolicyEngineResource(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        String policyEngineAdapterUrl = policyEngineResource.getAdapterUrl();
        ResponseEntity<ErrorRes> postResponse;

        // 42201 - PolicyEngine is invalid - PolicyEngine adapterURL cannot be null
        policyEngineResource.setAdapterUrl(null);
        postResponse = policyClient.createPolicyEngineResponseEntity(policyEngineResource);
        verifyResponseError(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                "PolicyEngine adapterUrl cannot be null"
        );

        // 42201 - PolicyEngine is invalid - PolicyEngine name cannot be null
        policyEngineResource.setAdapterUrl(policyEngineAdapterUrl);
        policyEngineResource.setName(null);
        postResponse = policyClient.createPolicyEngineResponseEntity(policyEngineResource);
        verifyResponseError(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                "PolicyEngine name cannot be null"
        );

        // 42205 - PolicyEngine is invalid - PolicyEngine with name [" + policyEngine.getName() + "] already exists
        policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        policyEngineResource.setId(null);
        postResponse = policyClient.createPolicyEngineResponseEntity(policyEngineResource);
        verifyResponseError(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_05_POLICY_ENGINE_ALREADY_EXISTS,
                "PolicyEngine with name [" + policyEngineResource.getName() + "] already exists"
        );

    }

    // ======================================================================================
    // UPDATE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEngineError400xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);

        // 40001 - Empty PolicyEngine
        ResponseEntity<ErrorRes> putResponse = policyClient.updatePolicyEngineResponseEntity(policyEngineResource.getId(), null);
        verifyResponseError(
                putResponse,
                HttpStatus.BAD_REQUEST,
                PolicyApiStandardErrors.SC400_01_POLICY_ENGINE_IS_EMPTY,
                "PolicyEngine object cannot be null"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEngineError404xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource policyEngineResource = createPolicyEngineResource(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);

        // 40401 - PolicyEngine not found
        ResponseEntity putResponse = policyClient.updatePolicyEngineResponseEntity(2L, policyEngineResource);
        verifyResponseError(
                putResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND,
                "Resource with ID [2] not found"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEngineError422xx() throws JsonProcessingException {

        // Resources
        PolicyEngineResource policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        String policyEngineAdapterUrl = policyEngineResource.getAdapterUrl();
        ResponseEntity<ErrorRes> putResponse;

        // 42201 - PolicyEngine is invalid - PolicyEngine adapterURL cannot be null
        policyEngineResource.setAdapterUrl(null);
        putResponse = policyClient.updatePolicyEngineResponseEntity(policyEngineResource.getId(), policyEngineResource);
        verifyResponseError(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                "PolicyEngine adapterUrl cannot be null"
        );

        // 42201 - PolicyEngine is invalid - PolicyEngine name cannot be null
        policyEngineResource.setAdapterUrl(policyEngineAdapterUrl);
        policyEngineResource.setName(null);
        putResponse = policyClient.updatePolicyEngineResponseEntity(policyEngineResource.getId(), policyEngineResource);
        verifyResponseError(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                "PolicyEngine name cannot be null"
        );

    }


    // ======================================================================================
    // READ ALL PolicyEngines
    // ======================================================================================

    // No specific errors to test excluding 500

    // ======================================================================================
    // READ ONE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicyEngineError404xx() throws JsonProcessingException {

        // 40401 - Resource not found
        ResponseEntity getResponse = policyClient.readOnePolicyEngineResponseEntity(2L);
        verifyResponseError(
                getResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND,
                "Resource with ID [2] not found"
        );

    }

    // ======================================================================================
    // DELETE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteOnePolicyEngineError404xx() throws JsonProcessingException {

        // 40401 - Resource not found
        ResponseEntity deleteResponse = policyClient.deletePolicyEngineResponseEntity(2L);
        verifyResponseError(
                deleteResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND,
                "Resource with ID [2] not found"
        );

    }

}
