package org.opendatamesh.platform.pp.blueprint.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.api.resources.RepositoryProviderEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BlueprintIT extends ODMBlueprintIT {

    // ======================================================================================
    // CREATE Blueprint
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateBlueprintAllProperties() {

        BlueprintResource blueprintResource = createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);

        assertThat(blueprintResource.getId()).isNotNull();
        assertThat(blueprintResource.getName()).isEqualTo("azure-blueprint-1");
        assertThat(blueprintResource.getVersion()).isEqualTo("1.0.0");
        assertThat(blueprintResource.getDisplayName()).isEqualTo("blueprint 1");
        assertThat(blueprintResource.getDescription()).isEqualTo("First AzureDevOps Blueprint");
        assertThat(blueprintResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.AZURE_DEVOPS);
        assertThat(blueprintResource.getRepositoryUrl()).isEqualTo("http://azure-repo.com/repo");
        assertThat(blueprintResource.getBlueprintPath()).isEqualTo("/blueprints/blueprint-1");
        assertThat(blueprintResource.getTargetPath()).isEqualTo("/target/project-1");
        assertThat(blueprintResource.getConfigurations()).size().isEqualTo(3);
        assertThat(blueprintResource.getConfigurations().get("parameter1")).isEqualTo("value_of_parameter1");
        assertThat(blueprintResource.getConfigurations().get("parameter2")).isEqualTo("value_of_parameter2");
        assertThat(blueprintResource.getConfigurations().get("parameter3")).isEqualTo("value_of_parameter3");
        assertThat(blueprintResource.getCreatedAt()).isNotNull();
        assertThat(blueprintResource.getUpdatedAt()).isNotNull();

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
        assertThat(blueprintResource.getName()).isEqualTo("azure-blueprint-2");
        assertThat(blueprintResource.getVersion()).isEqualTo("1.0.0");
        assertThat(blueprintResource.getDisplayName()).isEqualTo("blueprint 2");
        assertThat(blueprintResource.getDescription()).isEqualTo("Second AzureDevOps Blueprint");
        assertThat(blueprintResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.AZURE_DEVOPS);
        assertThat(blueprintResource.getRepositoryUrl()).isEqualTo("http://azure-repo.com/repo");
        assertThat(blueprintResource.getBlueprintPath()).isEqualTo("/blueprints/blueprint-2");
        assertThat(blueprintResource.getTargetPath()).isEqualTo("/target/project-2");
        assertThat(blueprintResource.getConfigurations()).size().isEqualTo(3);
        assertThat(blueprintResource.getConfigurations().get("parameter1")).isEqualTo("value_of_parameter1");
        assertThat(blueprintResource.getConfigurations().get("parameter2")).isEqualTo("value_of_parameter2");
        assertThat(blueprintResource.getConfigurations().get("parameter3")).isEqualTo("value_of_parameter3");
        assertThat(blueprintResource.getCreatedAt()).isNotNull();
        assertThat(blueprintResource.getUpdatedAt()).isNotNull();

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
    public void testUpdateBlueprint() throws JsonProcessingException {

        BlueprintResource oldBlueprintResource = createBlueprint(ODMBlueprintResources.RESOURCE_BLUEPRINT_1);
        BlueprintResource blueprintResource = createBlueprintResource(
                ODMBlueprintResources.RESOURCE_BLUEPRINT_1_UPDATED
        );

        ResponseEntity<BlueprintResource> updateResponse = blueprintClient.updateBlueprint(
                oldBlueprintResource.getId(),
                blueprintResource
        );
        verifyResponseEntity(updateResponse, HttpStatus.OK, true);
        blueprintResource = updateResponse.getBody();

        ResponseEntity<BlueprintResource> readResponse = blueprintClient.readOneBlueprint(blueprintResource.getId());
        blueprintResource = readResponse.getBody();

        assertThat(blueprintResource.getId()).isNotNull();
        assertThat(blueprintResource.getName()).isEqualTo("azure-blueprint-1");
        assertThat(blueprintResource.getVersion()).isEqualTo("1.0.1");
        assertThat(blueprintResource.getDisplayName()).isEqualTo("blueprint 1");
        assertThat(blueprintResource.getDescription()).isEqualTo("First AzureDevOps Blueprint");
        assertThat(blueprintResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.AZURE_DEVOPS);
        assertThat(blueprintResource.getRepositoryUrl()).isEqualTo("http://azure-repo.com/repo-1");
        assertThat(blueprintResource.getBlueprintPath()).isEqualTo("/blueprints/blueprint-1/v1.0.1");
        assertThat(blueprintResource.getTargetPath()).isEqualTo("/target/project-1/v1.0.1");
        assertThat(blueprintResource.getConfigurations()).size().isEqualTo(2);
        assertThat(blueprintResource.getConfigurations().get("parameter1")).isEqualTo("value_of_parameter1-updated");
        assertThat(blueprintResource.getConfigurations().get("parameter2")).isEqualTo("value_of_parameter2");
        assertThat(blueprintResource.getCreatedAt()).isEqualTo(oldBlueprintResource.getCreatedAt());
        assertThat(blueprintResource.getUpdatedAt()).isNotNull();
        assertThat(blueprintResource.getUpdatedAt()).isAfter(oldBlueprintResource.getCreatedAt());
        assertThat(blueprintResource.getUpdatedAt()).isAfter(oldBlueprintResource.getUpdatedAt());

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
        assertThat(blueprintResource.getName()).isEqualTo("azure-blueprint-1");
        assertThat(blueprintResource.getVersion()).isEqualTo("1.0.0");
        assertThat(blueprintResource.getDisplayName()).isEqualTo("blueprint 1");
        assertThat(blueprintResource.getDescription()).isEqualTo("First AzureDevOps Blueprint");
        assertThat(blueprintResource.getRepositoryProvider()).isEqualTo(RepositoryProviderEnum.AZURE_DEVOPS);
        assertThat(blueprintResource.getRepositoryUrl()).isEqualTo("http://azure-repo.com/repo");
        assertThat(blueprintResource.getBlueprintPath()).isEqualTo("/blueprints/blueprint-1");
        assertThat(blueprintResource.getTargetPath()).isEqualTo("/target/project-1");
        assertThat(blueprintResource.getConfigurations()).size().isEqualTo(3);
        assertThat(blueprintResource.getConfigurations().get("parameter1")).isEqualTo("value_of_parameter1");
        assertThat(blueprintResource.getConfigurations().get("parameter2")).isEqualTo("value_of_parameter2");
        assertThat(blueprintResource.getConfigurations().get("parameter3")).isEqualTo("value_of_parameter3");
        assertThat(blueprintResource.getCreatedAt()).isNotNull();
        assertThat(blueprintResource.getUpdatedAt()).isNotNull();
    }
}
