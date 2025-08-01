package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyEngineErrorsIT extends ODMPolicyIT {

    // ======================================================================================
    // CREATE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyEngineError400xx() throws JsonProcessingException {

        // 40001 - Empty PolicyEngine
        ResponseEntity<ObjectNode> postResponse = policyClient.createPolicyEngineResponseEntity(null);
        verifyResponseErrorObjectNode(
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
        ResponseEntity<ObjectNode> postResponse;

        // 42201 - PolicyEngine is invalid - PolicyEngine adapterURL cannot be null
        policyEngineResource.setAdapterUrl(null);
        postResponse = policyClient.createPolicyEngineResponseEntity(policyEngineResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                "PolicyEngine adapterUrl cannot be null"
        );

        // 42201 - PolicyEngine is invalid - PolicyEngine name cannot be null
        policyEngineResource.setAdapterUrl(policyEngineAdapterUrl);
        policyEngineResource.setName(null);
        postResponse = policyClient.createPolicyEngineResponseEntity(policyEngineResource);
        verifyResponseErrorObjectNode(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                "PolicyEngine name cannot be null"
        );

        // 42205 - PolicyEngine is invalid - PolicyEngine with name [" + policyEngine.getName() + "] already exists
        policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        policyEngineResource.setId(null);
        postResponse = policyClient.createPolicyEngineResponseEntity(policyEngineResource);
        verifyResponseErrorObjectNode(
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
        ResponseEntity<ObjectNode> putResponse = policyClient.updatePolicyEngineResponseEntity(policyEngineResource.getId(), null);
        verifyResponseErrorObjectNode(
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
        verifyResponseErrorObjectNode(
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
        ResponseEntity<ObjectNode> putResponse;

        // 42201 - PolicyEngine is invalid - PolicyEngine adapterURL cannot be null
        policyEngineResource.setAdapterUrl(null);
        putResponse = policyClient.updatePolicyEngineResponseEntity(policyEngineResource.getId(), policyEngineResource);
        verifyResponseErrorObjectNode(
                putResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                "PolicyEngine adapterUrl cannot be null"
        );

        // 42201 - PolicyEngine is invalid - PolicyEngine name cannot be null
        policyEngineResource.setAdapterUrl(policyEngineAdapterUrl);
        policyEngineResource.setName(null);
        putResponse = policyClient.updatePolicyEngineResponseEntity(policyEngineResource.getId(), policyEngineResource);
        verifyResponseErrorObjectNode(
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
        verifyResponseErrorObjectNode(
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
        verifyResponseErrorObjectNode(
                deleteResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND,
                "Resource with ID [2] not found"
        );

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEngineToExistingName() {

        // Create first policy engine with name "policy-engine-1"
        PolicyEngineResource policyEngine1 = new PolicyEngineResource();
        policyEngine1.setName("opa-policy-checker");
        policyEngine1.setDisplayName("OPA Policy Checker");
        policyEngine1.setAdapterUrl("http://localhost:9009");

        // Create first policy engine via REST
        ResponseEntity<PolicyEngineResource> postResponse1 = rest.postForEntity(
                apiUrl("/api/v1/pp/policy/policy-engines"),
                policyEngine1,
                PolicyEngineResource.class
        );
        policyEngine1 = postResponse1.getBody();

        // Create second policy engine with name "policy-engine-2"
        PolicyEngineResource policyEngine2 = new PolicyEngineResource();
        policyEngine2.setName("opa-policy-checker1");
        policyEngine2.setDisplayName("OPA Policy Checker");
        policyEngine2.setAdapterUrl("http://localhost:9009");

        ResponseEntity<PolicyEngineResource> postResponse2 = rest.postForEntity(
                apiUrl("/api/v1/pp/policy/policy-engines"),
                policyEngine2,
                PolicyEngineResource.class
        );
        policyEngine2 = postResponse2.getBody();

        // Modify the second policy engine to change its name to "policy-engine-1"
        PolicyEngineResource policyEngine3 = new PolicyEngineResource();
        policyEngine3.setName("opa-policy-checker");
        policyEngine3.setDisplayName("OPA Policy Checker");
        policyEngine3.setAdapterUrl("http://localhost:9009");

        // Try to update the second policy engine with the same name as the first
        ResponseEntity<ObjectNode> putResponse = rest.exchange(
                apiUrl("/api/v1/pp/policy/policy-engines/" + policyEngine2.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(policyEngine3),
                ObjectNode.class
        );

        // Verify the error response using assertThat
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        ObjectNode errorBody = putResponse.getBody();
        assertThat(errorBody).isNotNull();
        assertThat(errorBody.get("code").asText()).isEqualTo(PolicyApiStandardErrors.SC422_05_POLICY_ENGINE_ALREADY_EXISTS.code());
        assertThat(errorBody.get("message").asText()).isEqualTo("PolicyEngine with name [opa-policy-checker] already exists");

    }

}
