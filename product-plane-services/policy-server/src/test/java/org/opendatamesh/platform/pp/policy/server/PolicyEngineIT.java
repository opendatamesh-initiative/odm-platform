package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
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
    public void testCreatePolicyEngineAllProperties() {

        // Resources + Creation
        PolicyEngineResource policyEngineResource = createPolicyEngine(ODMPolicyResources.RESOURCE_POLICY_ENGINE_1);

        // Verification
        verifyResourcePolicyEngineOne(policyEngineResource);

    }


    // ======================================================================================
    // READ ALL PolicyEngines
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllPolicies() throws JsonProcessingException {

        // Resources + Creation
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_1);
        createPolicy(ODMPolicyResources.RESOURCE_POLICY_2);

        // GET request
        ResponseEntity<PolicyResource[]> readResponse = policyClient.readAllPolicies();
        verifyResponseEntity(readResponse, HttpStatus.OK, true);
        List<PolicyResource> policies = List.of(readResponse.getBody());

        // Verification
        assertThat(policies).size().isEqualTo(2);
        //verifyResourcePolicyOne(policies.get(0));
        verifyResourcePolicyTwo(policies.get(1));

    }


    // ======================================================================================
    // READ ONE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicy() throws JsonProcessingException {

        // Resources + Creation
        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1);

        // GET request
        ResponseEntity<PolicyResource> readResponse = policyClient.readOnePolicy(policyResource.getId());
        verifyResponseEntity(readResponse, HttpStatus.OK, true);
        policyResource = readResponse.getBody();

        // Verification
        //verifyResourcePolicyOne(policyResource);

    }


    // ======================================================================================
    // UPDATE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicy() throws JsonProcessingException, InterruptedException {

    }


    // ======================================================================================
    // DELETE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeletePolicy() throws JsonProcessingException {


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

    private void verifyResourcePolicyOneUpdated(PolicyResource oldPolicyResource, PolicyResource policyResource) {

        assertThat(policyResource.getId()).isNotNull();
        assertThat(policyResource.getRootId()).isEqualTo(1);
        assertThat(policyResource.getName()).isEqualTo("dataproduct-name-checker");
        assertThat(policyResource.getDisplayName()).isEqualTo("Data Product Name Checker");
        assertThat(policyResource.getDescription()).isEqualTo("Check the Data Product name");
        assertThat(policyResource.getBlockingFlag()).isEqualTo(false);
        assertThat(policyResource.getRawContent()).isEqualTo("package dataproduct-name-checker\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
        assertThat(policyResource.getSuite()).isEqualTo("CREATION");
        //assertThat(policyResource.getIsLastVersion()).isEqualTo(true);
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isAfter(policyResource.getCreatedAt());

    }

    private void verifyResourcePolicyTwo(PolicyResource policyResource) {

        assertThat(policyResource.getId()).isNotNull();
        assertThat(policyResource.getRootId()).isEqualTo(policyResource.getId());
        assertThat(policyResource.getName()).isEqualTo("dataproduct-name-checker");
        assertThat(policyResource.getDisplayName()).isEqualTo("Data Product Name Checker");
        assertThat(policyResource.getDescription()).isEqualTo("Check whether the name of the input Data Product is compliant with global naming convention or not");
        assertThat(policyResource.getBlockingFlag()).isEqualTo(false);
        assertThat(policyResource.getRawContent()).isEqualTo("package dataproduct-name-checker\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}");
        assertThat(policyResource.getSuite()).isEqualTo("CREATION");
        //assertThat(policyResource.getIsLastVersion()).isEqualTo(true);
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isEqualTo(policyResource.getCreatedAt());

    }

}
