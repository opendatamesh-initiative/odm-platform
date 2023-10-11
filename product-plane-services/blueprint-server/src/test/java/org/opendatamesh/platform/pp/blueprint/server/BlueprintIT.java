package org.opendatamesh.platform.pp.blueprint.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.springframework.test.annotation.DirtiesContext;

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
        assertThat(blueprintResource.getRepositoryProvider()).isEqualTo("AZURE_DEVOPS");
        assertThat(blueprintResource.getRepositoryUrl()).isEqualTo("http://azure-repo.com/repo");
        assertThat(blueprintResource.getBlueprintPath()).isEqualTo("/blueprints/blueprint-1");
        assertThat(blueprintResource.getTargetPath()).isEqualTo("/target/project-1");
        assertThat(blueprintResource.getConfigurations()).size().isEqualTo(3);
        assertThat(blueprintResource.getConfigurations().get(0)).isEqualTo("value_of_parameter1");
        assertThat(blueprintResource.getConfigurations().get(1)).isEqualTo("value_of_parameter2");
        assertThat(blueprintResource.getConfigurations().get(2)).isEqualTo("value_of_parameter3");
        assertThat(blueprintResource.getCreatedAt()).isNotNull();
        assertThat(blueprintResource.getUpdatedAt()).isNull();

    }

}
