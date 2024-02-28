package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PagedPolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
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
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1);

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
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1);
        PolicyEvaluationResultResource policyEvaluationResultResourceUpdated = createPolicyEvaluationResultResource(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1_UPDATED);
        // TODO: discuss update strategies (ID and CreationTime actually MUST be in the updated object and i don't love it)
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
        createPolicyEvaluationResult(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1);
        createPolicyEvaluationResult(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_2);

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
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1);

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
        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResult(ODMPolicyResources.RESOURCE_POLICY_EVALUATION_RESULT_1);

        // DELETE request
        ResponseEntity<Void> deleteResponse = policyClient.deletePolicyEvaluationResult(policyEvaluationResultResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        // GET request to check that the entity is not on the DB anymore
        ResponseEntity<ErrorRes> getResponse = policyClient.readOnePolicyEvaluationResult(policyEvaluationResultResource.getId());
        verifyResponseError(
                getResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND
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
        assertThat(policyEvaluationResultResource.getUpdatedAt()).isEqualTo(policyEvaluationResultResource.getCreatedAt());

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
