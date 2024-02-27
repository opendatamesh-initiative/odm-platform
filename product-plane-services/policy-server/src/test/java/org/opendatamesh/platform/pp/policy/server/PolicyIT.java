package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PolicyIT extends ODMPolicyIT {

    // ======================================================================================
    // CREATE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreatePolicyAllProperties() {

        // Resources + Creation
        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1);

        // Verification
        verifyResourcePolicyOne(policyResource);

    }


    // ======================================================================================
    // READ ALL Policies
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
        verifyResourcePolicyOne(policies.get(0));
        verifyResourcePolicyTwo(policies.get(1));

    }


    // ======================================================================================
    // READ ONE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOnePolicy() throws JsonProcessingException {

        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1);
        ResponseEntity<PolicyResource> readResponse = policyClient.readOnePolicy(policyResource.getId());
        policyResource = readResponse.getBody();

        verifyResourcePolicyOne(policyResource);

    }


    // ======================================================================================
    // UPDATE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdatePolicy() throws JsonProcessingException, InterruptedException {

        PolicyResource oldPolicyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1);

        System.out.println(oldPolicyResource);

        PolicyResource policyResource = createPolicyResource(
                ODMPolicyResources.RESOURCE_POLICY_1_UPDATED
        );

        // To check update timestamp greater than creation timestamp
        TimeUnit.SECONDS.sleep(2);

        ResponseEntity<PolicyResource> updateResponse = policyClient.updatePolicy(
                oldPolicyResource.getId(),
                policyResource
        );
        verifyResponseEntity(updateResponse, HttpStatus.OK, true);
        policyResource = updateResponse.getBody();

        ResponseEntity<PolicyResource> readResponse = policyClient.readOnePolicy(policyResource.getId());
        policyResource = readResponse.getBody();

        System.out.println(policyResource);

        assertThat(policyResource.getId()).isNotNull();
        assertThat(policyResource.getName()).isEqualTo("github-policy-1");
        assertThat(policyResource.getVersion()).isEqualTo("1.0.1");
        assertThat(policyResource.getDisplayName()).isEqualTo("policy 1");
        assertThat(policyResource.getDescription()).isEqualTo("First GitHub Policy");
        assertThat(policyResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.GITHUB);
        assertThat(policyResource.getRepositoryUrl()).isEqualTo("git@github.com:opendatamesh-initiative/policy1.1.git");
        assertThat(policyResource.getPolicyDirectory()).isEqualTo("policy");
        assertThat(policyResource.getOrganization()).isEqualTo("opendatamesh-initiative");
        assertThat(policyResource.getCreatedAt()).isEqualTo(oldPolicyResource.getCreatedAt());
        assertThat(policyResource.getUpdatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isAfter(oldPolicyResource.getCreatedAt());

    }


    // ======================================================================================
    // DELETE Policy
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeletePolicy() throws JsonProcessingException {

        PolicyResource policyResource = createPolicy(ODMPolicyResources.RESOURCE_POLICY_1);
        ResponseEntity<Void> deleteResponse = policyClient.deletePolicy(policyResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        ResponseEntity<PolicyResource[]> readResponse = policyClient.readAllPolicies();
        List<PolicyResource> policyResourceList = List.of(readResponse.getBody());

        assertThat(policyResourceList).isEmpty();

    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourcePolicyOne(PolicyResource policyResource) {
        assertThat(policyResource.getId()).isNotNull();
        assertThat(policyResource.getName()).isEqualTo("github-policy-1");
        assertThat(policyResource.getVersion()).isEqualTo("1.0.0");
        assertThat(policyResource.getDisplayName()).isEqualTo("policy 1");
        assertThat(policyResource.getDescription()).isEqualTo("First GitHub Policy");
        assertThat(policyResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.GITHUB);
        assertThat(policyResource.getRepositoryUrl()).isEqualTo("git@github.com:opendatamesh-initiative/policy1.git");
        assertThat(policyResource.getPolicyDirectory()).isEqualTo("policy");
        assertThat(policyResource.getOrganization()).isEqualTo("opendatamesh-initiative");
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isNull();
    }

    private void verifyResourcePolicyOne(PolicyResource policyResource) {
        assertThat(policyResource.getId()).isNotNull();
        assertThat(policyResource.getName()).isEqualTo("github-policy-1");
        assertThat(policyResource.getVersion()).isEqualTo("1.0.0");
        assertThat(policyResource.getDisplayName()).isEqualTo("policy 1");
        assertThat(policyResource.getDescription()).isEqualTo("First GitHub Policy");
        assertThat(policyResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.GITHUB);
        assertThat(policyResource.getRepositoryUrl()).isEqualTo("git@github.com:opendatamesh-initiative/policy1.git");
        assertThat(policyResource.getPolicyDirectory()).isEqualTo("policy");
        assertThat(policyResource.getOrganization()).isEqualTo("opendatamesh-initiative");
        assertThat(policyResource.getCreatedAt()).isNotNull();
        assertThat(policyResource.getUpdatedAt()).isNull();
    }
    
}
