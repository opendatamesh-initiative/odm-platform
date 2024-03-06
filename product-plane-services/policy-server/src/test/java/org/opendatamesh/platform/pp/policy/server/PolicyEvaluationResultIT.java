package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PagedPolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyEvaluationResultIT extends ODMPolicyIT {

    // ======================================================================================
    // CREATE PolicyEvaluationResult
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyEvaluationResult() {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1,
                parentPolicyResource.getId()
        );

        // Verification
        verifyResourcePolicyEvaluationResultOne(policyEvaluationResultResource);

    }

    // ======================================================================================
    // UPDATE PolicyEvaluationResult
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicyEvaluationResult() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1,
                parentPolicyResource.getId()
        );
        PolicyEvaluationResultResource policyEvaluationResultResourceUpdated = createPolicyEvaluationResultResource(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1_UPDATED
        );
        // TODO: discuss update strategies (ID and CreationTime actually MUST be in the updated object)
        policyEvaluationResultResourceUpdated.setId(policyEvaluationResultResource.getId());
        policyEvaluationResultResourceUpdated.setCreatedAt(policyEvaluationResultResource.getCreatedAt());

        // PUT request
        ResponseEntity<PolicyEvaluationResultResource> putResponse = policyClient.updatePolicyEvaluationResult(
                policyEvaluationResultResource.getId(),
                policyEvaluationResultResourceUpdated
        );
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        policyEvaluationResultResourceUpdated = putResponse.getBody();

        // Verification
        verifyResourcePolicyEvaluationResultOneUpdated(policyEvaluationResultResourceUpdated);

    }


    // ======================================================================================
    // READ ALL PolicyEvaluationResults
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllPolicies() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        createPolicyEvaluationResult(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1, parentPolicyResource.getId());
        createPolicyEvaluationResult(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_2, parentPolicyResource.getId());

        // GET request
        ResponseEntity<PagedPolicyEvaluationResultResource> getResponse = policyClient.readAllPolicyEvaluationResults();
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<PolicyEvaluationResultResource> policyEvaluationResults = getResponse.getBody().getContent();

        // Verification
        assertThat(policyEvaluationResults).size().isEqualTo(2);
        verifyResourcePolicyEvaluationResultOne(policyEvaluationResults.get(0));
        verifyResourcePolicyEvaluationResultTwo(policyEvaluationResults.get(1));

    }


    // ======================================================================================
    // READ ONE PolicyEvaluationResult
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicyEvaluationResult() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1,
                parentPolicyResource.getId()
        );

        // GET request
        ResponseEntity<PolicyEvaluationResultResource> getResponse = policyClient.readOnePolicyEvaluationResult(policyEvaluationResultResource.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        policyEvaluationResultResource = getResponse.getBody();

        // Verification
        verifyResourcePolicyEvaluationResultOne(policyEvaluationResultResource);

    }


    // ======================================================================================
    // DELETE PolicyEvaluationResult
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeletePolicyEvaluationResult() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource parentPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(
                ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1,
                parentPolicyResource.getId()
        );

        // DELETE request
        ResponseEntity<Void> deleteResponse = policyClient.deletePolicyEvaluationResult(policyEvaluationResultResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        // GET request to check that the entity is not on the DB anymore
        ResponseEntity<ErrorRes> getResponse = policyClient.readOnePolicyEvaluationResult(policyEvaluationResultResource.getId());
        verifyResponseError(
                getResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND,
                "PolicyEvaluationResult with ID [" + policyEvaluationResultResource.getId() + "] not found"
        );

    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourcePolicyEvaluationResultOne(PolicyEvaluationResultResource policyEvaluationResultResource) {

        assertThat(policyEvaluationResultResource.getId()).isNotNull();
        assertThat(policyEvaluationResultResource.getPolicyId()).isEqualTo(1);
        assertThat(policyEvaluationResultResource.getResult()).isEqualTo(true);
        assertThat(policyEvaluationResultResource.getDataProductId()).isEqualTo("abc123");
        assertThat(policyEvaluationResultResource.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(policyEvaluationResultResource.getInputObject()).isEqualTo("{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}");
        assertThat(policyEvaluationResultResource.getOutputObject()).isEqualTo("{\"allow\":true}");
        assertThat(policyEvaluationResultResource.getCreatedAt()).isNotNull();
        assertThat(policyEvaluationResultResource.getUpdatedAt()).isEqualTo(policyEvaluationResultResource.getCreatedAt());

    }

    private void verifyResourcePolicyEvaluationResultOneUpdated(PolicyEvaluationResultResource policyEvaluationResultResource) {

        assertThat(policyEvaluationResultResource.getId()).isNotNull();
        assertThat(policyEvaluationResultResource.getPolicyId()).isEqualTo(1);
        assertThat(policyEvaluationResultResource.getResult()).isEqualTo(false);
        assertThat(policyEvaluationResultResource.getDataProductId()).isEqualTo("abc123");
        assertThat(policyEvaluationResultResource.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(policyEvaluationResultResource.getInputObject()).isEqualTo("{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}");
        assertThat(policyEvaluationResultResource.getOutputObject()).isEqualTo("{\"allow\":false}");
        assertThat(policyEvaluationResultResource.getCreatedAt()).isNotNull();
        assertThat(policyEvaluationResultResource.getUpdatedAt()).isAfter(policyEvaluationResultResource.getCreatedAt());

    }

    private void verifyResourcePolicyEvaluationResultTwo(PolicyEvaluationResultResource policyEvaluationResultResource) {

        assertThat(policyEvaluationResultResource.getId()).isNotNull();
        assertThat(policyEvaluationResultResource.getPolicyId()).isEqualTo(1);
        assertThat(policyEvaluationResultResource.getResult()).isEqualTo(true);
        assertThat(policyEvaluationResultResource.getDataProductId()).isEqualTo("def456");
        assertThat(policyEvaluationResultResource.getDataProductVersion()).isEqualTo("1.7.14");
        assertThat(policyEvaluationResultResource.getInputObject()).isEqualTo("{\"name\":\"dp-1-7-14\",\"description\":\"DataProduct1714Draft\",\"domain\":\"Sales\"}");
        assertThat(policyEvaluationResultResource.getOutputObject()).isEqualTo("{\"allow\":true}");
        assertThat(policyEvaluationResultResource.getCreatedAt()).isNotNull();
        assertThat(policyEvaluationResultResource.getUpdatedAt()).isEqualTo(policyEvaluationResultResource.getCreatedAt());

    }

}
