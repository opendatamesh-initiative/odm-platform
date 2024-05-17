package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyIT extends ODMPolicyIT {

    // ======================================================================================
    // CREATE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicy() {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());

        // Verification
        verifyResourcePolicyOne(policyResource, true);

    }


    // ======================================================================================
    // UPDATE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicy() throws JsonProcessingException, InterruptedException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyResource updatedPolicyResource = createPolicyResource(ODMPolicyResources.RESOURCE_POLICY_1_UPDATED);
        updatedPolicyResource.setPolicyEngine(parentEngineResource);
        updatedPolicyResource.setRootId(policyResource.getRootId());
        updatedPolicyResource.setCreatedAt(policyResource.getCreatedAt());

        // PUT request
        ResponseEntity<ObjectNode> putResponse = policyClient.updatePolicyResponseEntity(
                policyResource.getRootId(),
                updatedPolicyResource
        );
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        updatedPolicyResource = mapper.convertValue(putResponse.getBody(), PolicyResource.class);

        // GET request to check the previous version too
        ResponseEntity<ObjectNode> getResponse = policyClient.readOnePolicyVersionResponseEntity(policyResource.getId());
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        policyResource = mapper.convertValue(getResponse.getBody(), PolicyResource.class);

        // Verification
        verifyResourcePolicyOneUpdated(policyResource, updatedPolicyResource);

    }


    // ======================================================================================
    // READ ALL Policies
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllPolicies() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_2);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_2, parentEngineResource.getId());

        // GET request
        ResponseEntity<ObjectNode> readResponse = policyClient.readAllPoliciesResponseEntity();
        verifyResponseEntity(readResponse, HttpStatus.OK, true);
        List<PolicyResource> policies = extractListFromPageFromObjectNode(readResponse.getBody(), PolicyResource.class);

        // Verification
        assertThat(policies).size().isEqualTo(2);
        verifyResourcePolicyOne(policies.get(0), true);
        verifyResourcePolicyTwo(policies.get(1));

    }


    // ======================================================================================
    // READ ONE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicy() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());

        // GET request
        ResponseEntity<ObjectNode> readResponse = policyClient.readOnePolicyResponseEntity(policyResource.getRootId());
        verifyResponseEntity(readResponse, HttpStatus.OK, true);
        policyResource = mapper.convertValue(readResponse.getBody(), PolicyResource.class);

        // Verification
        verifyResourcePolicyOne(policyResource, true);

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicyByRootIdWhenMultipleVersionsExists() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyResource updatedPolicyResource = createPolicyResource(ODMPolicyResources.RESOURCE_POLICY_1_UPDATED);
        updatedPolicyResource.setPolicyEngine(parentEngineResource);
        updatedPolicyResource.setRootId(policyResource.getRootId());
        updatedPolicyResource.setCreatedAt(policyResource.getCreatedAt());
        ResponseEntity<ObjectNode> putResponse = policyClient.updatePolicyResponseEntity(
                policyResource.getRootId(),
                updatedPolicyResource
        );
        updatedPolicyResource = mapper.convertValue(putResponse.getBody(), PolicyResource.class);

        // GET request with RootID
        ResponseEntity<ObjectNode> getResponse = policyClient.readOnePolicyResponseEntity(updatedPolicyResource.getRootId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        updatedPolicyResource = mapper.convertValue(getResponse.getBody(), PolicyResource.class);

        // Verification
        verifyResourcePolicyOneUpdated(updatedPolicyResource, policyResource.getRootId());

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicyByVersionIdWhenMultipleVersionsExists() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());
        PolicyResource updatedPolicyResource = createPolicyResource(ODMPolicyResources.RESOURCE_POLICY_1_UPDATED);
        updatedPolicyResource.setPolicyEngine(parentEngineResource);
        updatedPolicyResource.setRootId(policyResource.getRootId());
        updatedPolicyResource.setCreatedAt(policyResource.getCreatedAt());
        policyClient.updatePolicyResponseEntity(policyResource.getRootId(), updatedPolicyResource);

        // GET request with ID of the previous policy (before update)
        ResponseEntity<ObjectNode> getResponse = policyClient.readOnePolicyVersionResponseEntity(policyResource.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        policyResource = mapper.convertValue(getResponse.getBody(), PolicyResource.class);

        // Verification
        verifyResourcePolicyOne(policyResource, false);

    }


    // ======================================================================================
    // DELETE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeletePolicy() throws JsonProcessingException {

        // Resources + Creation
        PolicyEngineResource parentEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);
        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1, parentEngineResource.getId());

        // DELETE request
        ResponseEntity<ObjectNode> deleteResponse = policyClient.deletePolicyResponseEntity(policyResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        // GET request to check that the entity is not on the DB anymore
        ResponseEntity<ObjectNode> getResponse = policyClient.readOnePolicyResponseEntity(policyResource.getId());
        verifyResponseErrorObjectNode(
                getResponse,
                HttpStatus.NOT_FOUND,
                PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND,
                "Resource with ID [" + policyResource.getId() + "] not found"
        );

    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourcePolicyOne(PolicyResource policyResource, Boolean isLastVersion) {

        assertThat(policyResource.getId()).isNotNull();
        assertThat(policyResource.getRootId()).isEqualTo(policyResource.getId());
        assertThat(policyResource.getName()).isEqualTo("dataproduct-name-checker");
        assertThat(policyResource.getDisplayName()).isEqualTo("Data Product Name Checker");
        assertThat(policyResource.getDescription()).isEqualTo("Check whether the name of the input Data Product is compliant with global naming convention or not");
        assertThat(policyResource.getBlockingFlag()).isEqualTo(true);
        assertThat(policyResource.getRawContent()).isEqualTo("package dataproduct-name-checker\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
        assertThat(policyResource.getEvaluationEvent()).isEqualTo("DATA_PRODUCT_CREATION");
        assertThat(policyResource.getSuite()).isEqualTo("Suite Name");
        assertThat(policyResource.getLastVersion()).isEqualTo(isLastVersion);
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isAfterOrEqualTo(policyResource.getCreatedAt());

    }

    private void verifyResourcePolicyOneUpdated(PolicyResource oldPolicyResource, PolicyResource policyResource) {

        verifyResourcePolicyOne(oldPolicyResource, false);

        assertThat(policyResource.getId()).isGreaterThan(oldPolicyResource.getId());
        assertThat(policyResource.getRootId()).isEqualTo(oldPolicyResource.getId());
        assertThat(policyResource.getName()).isEqualTo("dataproduct-name-checker");
        assertThat(policyResource.getDisplayName()).isEqualTo("Data Product Name Checker");
        assertThat(policyResource.getDescription()).isEqualTo("Check the Data Product name");
        assertThat(policyResource.getBlockingFlag()).isEqualTo(false);
        assertThat(policyResource.getRawContent()).isEqualTo("package dataproduct-name-checker\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
        assertThat(policyResource.getEvaluationEvent()).isEqualTo("DATA_PRODUCT_CREATION");
        assertThat(policyResource.getSuite()).isEqualTo("Suite Name");
        assertThat(policyResource.getLastVersion()).isEqualTo(true);
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isAfterOrEqualTo(policyResource.getCreatedAt());

    }

    public void verifyResourcePolicyOneUpdated(PolicyResource policyResource, Long rootId) {
        assertThat(policyResource.getId()).isGreaterThan(rootId);
        assertThat(policyResource.getRootId()).isEqualTo(rootId);
        verifyResourcePolicyOneUpdatedBaseline(policyResource);
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isAfterOrEqualTo(policyResource.getCreatedAt());
    }

    private void verifyResourcePolicyOneUpdatedBaseline(PolicyResource policyResource) {
        assertThat(policyResource.getName()).isEqualTo("dataproduct-name-checker");
        assertThat(policyResource.getDisplayName()).isEqualTo("Data Product Name Checker");
        assertThat(policyResource.getDescription()).isEqualTo("Check the Data Product name");
        assertThat(policyResource.getBlockingFlag()).isEqualTo(false);
        assertThat(policyResource.getRawContent()).isEqualTo("package dataproduct-name-checker\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
        assertThat(policyResource.getEvaluationEvent()).isEqualTo("DATA_PRODUCT_CREATION");
        assertThat(policyResource.getSuite()).isEqualTo("Suite Name");
        assertThat(policyResource.getLastVersion()).isEqualTo(true);
    }

    private void verifyResourcePolicyTwo(PolicyResource policyResource) {

        assertThat(policyResource.getId()).isNotNull();
        assertThat(policyResource.getRootId()).isEqualTo(policyResource.getId());
        assertThat(policyResource.getName()).isEqualTo("lambda-name-checker");
        assertThat(policyResource.getDisplayName()).isEqualTo("Custom Lambda Name Checker");
        assertThat(policyResource.getDescription()).isEqualTo("Check whether the name of the input Data Product is compliant with global naming convention or not");
        assertThat(policyResource.getBlockingFlag()).isEqualTo(false);
        assertThat(policyResource.getRawContent()).isNull();
        assertThat(policyResource.getEvaluationEvent()).isEqualTo("DATA_PRODUCT_CREATION");
        assertThat(policyResource.getSuite()).isEqualTo("Suite Name");
        assertThat(policyResource.getLastVersion()).isEqualTo(true);
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isAfterOrEqualTo(policyResource.getCreatedAt());

    }

}
