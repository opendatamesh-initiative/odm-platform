package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PagedPolicyResource;
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
        updatedPolicyResource.setPolicyEngineId(parentEngineResource.getId());
        updatedPolicyResource.setRootId(policyResource.getRootId());
        updatedPolicyResource.setCreatedAt(policyResource.getCreatedAt());

        // PUT request
        ResponseEntity<PolicyResource> putResponse = policyClient.updatePolicyResponseEntity(
                policyResource.getRootId(),
                updatedPolicyResource
        );
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        updatedPolicyResource = putResponse.getBody();

        // GET request to check the previous version too
        ResponseEntity<PolicyResource> getResponse = policyClient.readOnePolicyVersionResponseEntity(policyResource.getId());
        verifyResponseEntity(putResponse, HttpStatus.OK, true);
        policyResource = getResponse.getBody();

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
        ResponseEntity<PagedPolicyResource> readResponse = policyClient.readAllPoliciesResponseEntity();
        verifyResponseEntity(readResponse, HttpStatus.OK, true);
        List<PolicyResource> policies = readResponse.getBody().getContent();

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
        ResponseEntity<PolicyResource> readResponse = policyClient.readOnePolicyResponseEntity(policyResource.getRootId());
        verifyResponseEntity(readResponse, HttpStatus.OK, true);
        policyResource = readResponse.getBody();

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
        updatedPolicyResource.setPolicyEngineId(parentEngineResource.getId());
        updatedPolicyResource.setRootId(policyResource.getRootId());
        updatedPolicyResource.setCreatedAt(policyResource.getCreatedAt());
        ResponseEntity<PolicyResource> putResponse = policyClient.updatePolicyResponseEntity(
                policyResource.getRootId(),
                updatedPolicyResource
        );
        updatedPolicyResource = putResponse.getBody();

        // GET request with RootID
        ResponseEntity<PolicyResource> getResponse = policyClient.readOnePolicyResponseEntity(updatedPolicyResource.getRootId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        updatedPolicyResource = getResponse.getBody();

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
        updatedPolicyResource.setPolicyEngineId(parentEngineResource.getId());
        updatedPolicyResource.setRootId(policyResource.getRootId());
        updatedPolicyResource.setCreatedAt(policyResource.getCreatedAt());
        policyClient.updatePolicyResponseEntity(policyResource.getRootId(), updatedPolicyResource);

        // GET request with ID of the previous policy (before update)
        ResponseEntity<PolicyResource> getResponse = policyClient.readOnePolicyVersionResponseEntity(policyResource.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        policyResource = getResponse.getBody();

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
        ResponseEntity<Void> deleteResponse = policyClient.deletePolicyResponseEntity(policyResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        // GET request to check that the entity is not on the DB anymore
        ResponseEntity<ErrorRes> getResponse = policyClient.readOnePolicyResponseEntity(policyResource.getId());
        verifyResponseError(
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
        assertThat(policyResource.getSuite()).isEqualTo("DATA_PRODUCT_CREATION");
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
        assertThat(policyResource.getSuite()).isEqualTo("DATA_PRODUCT_CREATION");
        assertThat(policyResource.getLastVersion()).isEqualTo(true);
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isAfterOrEqualTo(policyResource.getCreatedAt());
        assertThat(policyResource.getUpdatedAt()).isAfterOrEqualTo(oldPolicyResource.getCreatedAt());

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
        assertThat(policyResource.getSuite()).isEqualTo("DATA_PRODUCT_CREATION");
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
        assertThat(policyResource.getSuite()).isEqualTo("DATA_PRODUCT_CREATION");
        assertThat(policyResource.getLastVersion()).isEqualTo(true);
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isAfterOrEqualTo(policyResource.getCreatedAt());

    }
    
}
