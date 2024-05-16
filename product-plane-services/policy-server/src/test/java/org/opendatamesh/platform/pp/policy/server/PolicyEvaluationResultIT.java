package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
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
        policyEvaluationResultResourceUpdated.setPolicy(policyEvaluationResultResource.getPolicy());
        policyEvaluationResultResourceUpdated.setCreatedAt(policyEvaluationResultResource.getCreatedAt());

        // PUT request
        ResponseEntity<ObjectNode> putResponse = policyClient.updatePolicyEvaluationResultResponseEntity(
                policyEvaluationResultResource.getId(),
                policyEvaluationResultResourceUpdated
        );
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        policyEvaluationResultResourceUpdated = mapper.convertValue(putResponse.getBody(), PolicyEvaluationResultResource.class);

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
        ResponseEntity<ObjectNode> getResponse = policyClient.readAllPolicyEvaluationResultsResponseEntity();
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<PolicyEvaluationResultResource> policyEvaluationResults = extractListFromPageFromObjectNode(
                getResponse.getBody(), PolicyEvaluationResultResource.class
        );

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
        ResponseEntity<ObjectNode> getResponse = policyClient.readOnePolicyEvaluationResultResponseEntity(policyEvaluationResultResource.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        policyEvaluationResultResource = mapper.convertValue(getResponse.getBody(), PolicyEvaluationResultResource.class);

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
        ResponseEntity<ObjectNode> deleteResponse = policyClient.deletePolicyEvaluationResultResponseEntity(policyEvaluationResultResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        // GET request to check that the entity is not on the DB anymore
        ResponseEntity<ObjectNode> getResponse = policyClient.readOnePolicyEvaluationResultResponseEntity(policyEvaluationResultResource.getId());
        verifyResponseErrorObjectNode(
                getResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND,
                "Resource with ID [" + policyEvaluationResultResource.getId() + "] not found"
        );

    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourcePolicyEvaluationResultOne(PolicyEvaluationResultResource policyEvaluationResultResource) {

        assertThat(policyEvaluationResultResource.getId()).isNotNull();
        assertThat(policyEvaluationResultResource.getPolicyId()).isNotNull();
        assertThat(policyEvaluationResultResource.getResult()).isEqualTo(true);
        assertThat(policyEvaluationResultResource.getDataProductId()).isEqualTo("abc123");
        assertThat(policyEvaluationResultResource.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(policyEvaluationResultResource.getInputObject().textValue()).isEqualTo("{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}");
        assertThat(policyEvaluationResultResource.getOutputObject()).isEqualTo("{\"allow\":true}");
        assertThat(policyEvaluationResultResource.getCreatedAt()).isNotNull();
        assertThat(policyEvaluationResultResource.getUpdatedAt()).isEqualTo(policyEvaluationResultResource.getCreatedAt());

    }

    private void verifyResourcePolicyEvaluationResultOneUpdated(PolicyEvaluationResultResource policyEvaluationResultResource) {

        assertThat(policyEvaluationResultResource.getId()).isNotNull();
        assertThat(policyEvaluationResultResource.getPolicyId()).isNotNull();
        assertThat(policyEvaluationResultResource.getResult()).isEqualTo(false);
        assertThat(policyEvaluationResultResource.getDataProductId()).isEqualTo("abc123");
        assertThat(policyEvaluationResultResource.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(policyEvaluationResultResource.getInputObject().textValue()).isEqualTo("{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}");
        assertThat(policyEvaluationResultResource.getOutputObject()).isEqualTo("{\"allow\":false}");
        assertThat(policyEvaluationResultResource.getCreatedAt()).isNotNull();
        assertThat(policyEvaluationResultResource.getUpdatedAt()).isAfter(policyEvaluationResultResource.getCreatedAt());

    }

    private void verifyResourcePolicyEvaluationResultTwo(PolicyEvaluationResultResource policyEvaluationResultResource) {

        assertThat(policyEvaluationResultResource.getId()).isNotNull();
        assertThat(policyEvaluationResultResource.getPolicyId()).isNotNull();
        assertThat(policyEvaluationResultResource.getResult()).isEqualTo(true);
        assertThat(policyEvaluationResultResource.getDataProductId()).isEqualTo("def456");
        assertThat(policyEvaluationResultResource.getDataProductVersion()).isEqualTo("1.7.14");
        assertThat(policyEvaluationResultResource.getInputObject().textValue()).isEqualTo("{\"name\":\"dp-1-7-14\",\"description\":\"DataProduct1714Draft\",\"domain\":\"Sales\"}");
        assertThat(policyEvaluationResultResource.getOutputObject()).isEqualTo("{\"allow\":true}");
        assertThat(policyEvaluationResultResource.getCreatedAt()).isNotNull();
        assertThat(policyEvaluationResultResource.getUpdatedAt()).isEqualTo(policyEvaluationResultResource.getCreatedAt());

    }

}
