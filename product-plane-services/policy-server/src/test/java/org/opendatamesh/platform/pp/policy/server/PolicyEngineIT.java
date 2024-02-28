package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PagedPolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyEngineIT extends ODMPolicyIT {

    // ======================================================================================
    // CREATE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyEngine() {

        // Resources + Creation
        PolicyEngineResource policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);

        // Verification
        verifyResourcePolicyEngineOne(policyEngineResource);

    }

    // ======================================================================================
    // UPDATE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEngine() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyEngineResource policyEngineResourceUpdated = createPolicyEngineResource(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1_UPDATED);
        // TODO: discuss update strategies (ID and CreationTime actually MUST be in the updated object and i don't love it)
        policyEngineResourceUpdated.setId(policyEngineResource.getId());
        policyEngineResourceUpdated.setCreatedAt(policyEngineResource.getCreatedAt());

        // PUT request
        ResponseEntity<PolicyEngineResource> putResponse = policyClient.updatePolicyEngine(
                policyEngineResource.getId(),
                policyEngineResourceUpdated
        );
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        policyEngineResourceUpdated = putResponse.getBody();

        // Verification
        verifyResourcePolicyEngineOneUpdated(policyEngineResourceUpdated);

    }


    // ======================================================================================
    // READ ALL PolicyEngines
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllPolicies() throws JsonProcessingException {

        // Resources + Creation
        createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_2);

        // GET request
        ResponseEntity<PagedPolicyEngineResource> getResponse = policyClient.readAllPolicyEngines();
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<PolicyEngineResource> policyEngines = getResponse.getBody().getContent();

        // Verification
        assertThat(policyEngines).size().isEqualTo(2);
        verifyResourcePolicyEngineOne(policyEngines.get(0));
        verifyResourcePolicyEngineTwo(policyEngines.get(1));

    }


    // ======================================================================================
    // READ ONE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicyEngine() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);

        // GET request
        ResponseEntity<PolicyEngineResource> getResponse = policyClient.readOnePolicyEngine(policyEngineResource.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        policyEngineResource = getResponse.getBody();

        // Verification
        verifyResourcePolicyEngineOne(policyEngineResource);

    }


    // ======================================================================================
    // DELETE PolicyEngine
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeletePolicyEngine() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);

        // DELETE request
        ResponseEntity<Void> deleteResponse = policyClient.deletePolicyEngine(policyEngineResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        // GET request to check that the entity is not on the DB anymore
        ResponseEntity<ErrorRes> getResponse = policyClient.readOnePolicyEngine(policyEngineResource.getId());
        verifyResponseError(
                getResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND
        );

    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourcePolicyEngineOne(PolicyEngineResource policyEngineResource) {

        assertThat(policyEngineResource.getId()).isNotNull();
        assertThat(policyEngineResource.getName()).isEqualTo("opa-policy-checker");
        assertThat(policyEngineResource.getDisplayName()).isEqualTo("OPA Policy Checker");
        assertThat(policyEngineResource.getAdapterUrl()).isEqualTo("http://localhost:9001/api/v1/up/policy-engine-adapter");
        assertThat(policyEngineResource.getCreatedAt()).isNotNull();
        assertThat(policyEngineResource.getUpdatedAt()).isEqualTo(policyEngineResource.getCreatedAt());

    }

    private void verifyResourcePolicyEngineOneUpdated(PolicyEngineResource policyEngineResource) {

        assertThat(policyEngineResource.getId()).isNotNull();
        assertThat(policyEngineResource.getName()).isEqualTo("opa-policy-checker");
        assertThat(policyEngineResource.getDisplayName()).isEqualTo("OPA Policy Checker V2");
        assertThat(policyEngineResource.getAdapterUrl()).isEqualTo("http://localhost:9001/api/v1/up/policy-engine-adapter-2");
        assertThat(policyEngineResource.getCreatedAt()).isNotNull();
        assertThat(policyEngineResource.getUpdatedAt()).isAfter(policyEngineResource.getCreatedAt());

    }

    private void verifyResourcePolicyEngineTwo(PolicyEngineResource policyEngineResource) {

        assertThat(policyEngineResource.getId()).isNotNull();
        assertThat(policyEngineResource.getName()).isEqualTo("lambda-policy-checker");
        assertThat(policyEngineResource.getDisplayName()).isEqualTo("Custom Lambda Policy Checker");
        assertThat(policyEngineResource.getAdapterUrl()).isEqualTo("https://abcdefg.lambda-url.us-east-1.on.aws");
        assertThat(policyEngineResource.getCreatedAt()).isNotNull();
        assertThat(policyEngineResource.getUpdatedAt()).isEqualTo(policyEngineResource.getCreatedAt());

    }

}
