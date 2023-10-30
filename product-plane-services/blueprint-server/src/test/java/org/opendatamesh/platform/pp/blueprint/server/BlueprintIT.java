package org.opendatamesh.platform.pp.blueprint.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.api.resources.RepositoryProviderEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class BlueprintIT extends ODMBlueprintIT {

    // ======================================================================================
    // CREATE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateBlueprintAllProperties() {

        BlueprintResource blueprintResource = createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);

        verifyResourceBlueprintOne(blueprintResource);

    }


    // ======================================================================================
    // READ ALL Blueprints
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadAllBlueprints() throws JsonProcessingException {

        createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);
        createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_2);

        ResponseEntity<BlueprintResource[]> readResponse = blueprintClient.readBlueprints();
        verifyResponseEntity(readResponse, HttpStatus.OK, true);
        List<BlueprintResource> blueprints = List.of(readResponse.getBody());

        assertThat(blueprints).size().isEqualTo(2);

        verifyResourceBlueprintOne(blueprints.get(0));

        BlueprintResource blueprintResource = blueprints.get(1);

        assertThat(blueprintResource.getId()).isNotNull();
        assertThat(blueprintResource.getName()).isEqualTo("azure-blueprint");
        assertThat(blueprintResource.getVersion()).isEqualTo("1.0.0");
        assertThat(blueprintResource.getDisplayName()).isEqualTo("blueprint");
        assertThat(blueprintResource.getDescription()).isEqualTo("First AzureDevOps Blueprint");
        assertThat(blueprintResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.AZURE_DEVOPS);
        assertThat(blueprintResource.getRepositoryUrl()).isEqualTo("git@ssh.dev.azure.com:v3/organization/demoproject/blueprint");
        assertThat(blueprintResource.getOrganization()).isEqualTo("organization");
        assertThat(blueprintResource.getProjectName()).isEqualTo("demoproject");
        assertThat(blueprintResource.getCreatedAt()).isNotNull();
        assertThat(blueprintResource.getUpdatedAt()).isNull();

    }


    // ======================================================================================
    // READ ONE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testReadOneBlueprint() throws JsonProcessingException {

        BlueprintResource blueprintResource = createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);
        ResponseEntity<BlueprintResource> readResponse = blueprintClient.readOneBlueprint(blueprintResource.getId());
        blueprintResource = readResponse.getBody();

        verifyResourceBlueprintOne(blueprintResource);

    }


    // ======================================================================================
    // UPDATE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testUpdateBlueprint() throws JsonProcessingException, InterruptedException {

        BlueprintResource oldBlueprintResource = createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);

        System.out.println(oldBlueprintResource);

        BlueprintResource blueprintResource = createBlueprintResource(
                ODMBlueprintResources.RESOURCE_BLUEPRINT_1_UPDATED
        );

        // To check update timestamp greater than creation timestamp
        TimeUnit.SECONDS.sleep(2);

        ResponseEntity<BlueprintResource> updateResponse = blueprintClient.updateBlueprint(
                oldBlueprintResource.getId(),
                blueprintResource
        );
        verifyResponseEntity(updateResponse, HttpStatus.OK, true);
        blueprintResource = updateResponse.getBody();

        ResponseEntity<BlueprintResource> readResponse = blueprintClient.readOneBlueprint(blueprintResource.getId());
        blueprintResource = readResponse.getBody();

        System.out.println(blueprintResource);

        assertThat(blueprintResource.getId()).isNotNull();
        assertThat(blueprintResource.getName()).isEqualTo("github-blueprint-1");
        assertThat(blueprintResource.getVersion()).isEqualTo("1.0.1");
        assertThat(blueprintResource.getDisplayName()).isEqualTo("blueprint 1");
        assertThat(blueprintResource.getDescription()).isEqualTo("First GitHub Blueprint");
        assertThat(blueprintResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.GITHUB);
        assertThat(blueprintResource.getRepositoryUrl()).isEqualTo("git@github.com:opendatamesh-initiative/blueprint1.1.git");
        assertThat(blueprintResource.getOrganization()).isEqualTo("opendatamesh-initiative");
        assertThat(blueprintResource.getCreatedAt()).isEqualTo(oldBlueprintResource.getCreatedAt());
        assertThat(blueprintResource.getUpdatedAt()).isNotNull();
        assertThat(blueprintResource.getUpdatedAt()).isAfter(oldBlueprintResource.getCreatedAt());

    }


    // ======================================================================================
    // DELETE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteBlueprint() throws JsonProcessingException {

        BlueprintResource blueprintResource = createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);
        ResponseEntity<Void> deleteResponse = blueprintClient.deleteBlueprint(blueprintResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        ResponseEntity<BlueprintResource[]> readResponse = blueprintClient.readBlueprints();
        List<BlueprintResource> blueprintResourceList = List.of(readResponse.getBody());

        assertThat(blueprintResourceList).isEmpty();

    }


    // ======================================================================================
    // UTILS
    // ======================================================================================

    private void verifyResourceBlueprintOne(BlueprintResource blueprintResource) {
        assertThat(blueprintResource.getId()).isNotNull();
        assertThat(blueprintResource.getName()).isEqualTo("github-blueprint-1");
        assertThat(blueprintResource.getVersion()).isEqualTo("1.0.0");
        assertThat(blueprintResource.getDisplayName()).isEqualTo("blueprint 1");
        assertThat(blueprintResource.getDescription()).isEqualTo("First GitHub Blueprint");
        assertThat(blueprintResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.GITHUB);
        assertThat(blueprintResource.getRepositoryUrl()).isEqualTo("git@github.com:opendatamesh-initiative/blueprint1.git");
        assertThat(blueprintResource.getOrganization()).isEqualTo("opendatamesh-initiative");
        assertThat(blueprintResource.getCreatedAt()).isNotNull();
        assertThat(blueprintResource.getUpdatedAt()).isNull();
    }
}
