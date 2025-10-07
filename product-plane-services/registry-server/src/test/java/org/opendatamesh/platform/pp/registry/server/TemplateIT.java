package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.dpds.location.DescriptorLocation;
import org.opendatamesh.dpds.location.UriLocation;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.dpds.model.internals.LifecycleInfoDPDS;
import org.opendatamesh.dpds.model.internals.LifecycleTaskInfoDPDS;
import org.opendatamesh.dpds.parser.DPDSParser;
import org.opendatamesh.dpds.parser.IdentifierStrategyFactory;
import org.opendatamesh.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryTestResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class TemplateIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Template
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateTemplateWithAllProperties() {

        ExternalComponentResource template = null, template1 = null;

        template = resourceBuilder.buildTestTemplate();

        template1 = createTemplate(template);

        // Note: ID is now generated randomly, so we don't expect it to equal the input ID
        // assertThat(template1.getId()).isEqualTo(template.getId()); // Removed - IDs are now random
        assertThat(template1.getFullyQualifiedName()).isEqualTo(template.getFullyQualifiedName());
        assertThat(template1.getEntityType()).isEqualTo(template.getEntityType());
        assertThat(template1.getName()).isEqualTo(template.getName());
        assertThat(template1.getVersion()).isEqualTo(template.getVersion());
        assertThat(template1.getDisplayName()).isEqualTo(template.getDisplayName());
        assertThat(template1.getDescription()).isEqualTo(template.getDescription());
        assertThat(template1.getSpecification()).isEqualTo(template.getSpecification());
        assertThat(template1.getSpecificationVersion()).isEqualTo(template.getSpecificationVersion());
        assertThat(template1.getDefinitionMediaType()).isEqualTo(template.getDefinitionMediaType());
        assertThat(template1.getDefinition()).isEqualTo(template.getDefinition());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersionWithTemplates() {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = createDataProductVersion(createdDataProductRes.getId(), ODMRegistryTestResources.DPD_CORE);
        DataProductVersionDPDS dataProductVersion = null;
        System.out.println(descriptorContent);
        
        DPDSParser parser = new DPDSParser(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/schemas/",
                "1.0.0",
                null
        );
        DescriptorLocation location = new UriLocation(descriptorContent);
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost");
        options.setRewriteEntityType(false);
        options.setIdentifierStrategy(IdentifierStrategyFactory.getDefault());
        try {
            dataProductVersion = parser.parse(location, options).getDescriptorDocument();
        } catch (Throwable t) {
            fail("Impossible to parse descriptor content", t);
        }

        LifecycleInfoDPDS lifecycleInfo = dataProductVersion.getInternalComponents().getLifecycleInfo();
        List<LifecycleTaskInfoDPDS> tasksInfo = lifecycleInfo.getTasksInfo("test");
        LifecycleTaskInfoDPDS taskInfo = tasksInfo.get(0);
        StandardDefinitionDPDS templateStdDef = taskInfo.getTemplate();
        String templateId = templateStdDef.getId();

        ResponseEntity<ExternalComponentResource> response = null;

        response = registryClient.getTemplate(templateId);
        verifyResponseEntity(response, HttpStatus.OK, true);
        ExternalComponentResource template = response.getBody();
        assertThat(template.getFullyQualifiedName()).isEqualTo(templateStdDef.getFullyQualifiedName());
    
        ResponseEntity<String> templateResponse = registryClient.getTemplate(templateId, String.class);
        String templateContent = templateResponse.getBody();
        assertThat(templateContent).isNotNull(); 
        //System.out.println(templateContent);
    }


    // ======================================================================================
    // READ Api
    // ======================================================================================
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTemplates() {

        ExternalComponentResource template = null, template1 = null, template2 = null, template3 = null, template4 = null;

        template = resourceBuilder.buildTestTemplate();
        template1 = createTemplate(template);
        // Note: template1 will have a different ID (random) than template, so we can't use isEqualTo
        // Instead, verify the important properties are the same
        assertThat(template1.getFullyQualifiedName()).isEqualTo(template.getFullyQualifiedName());
        assertThat(template1.getName()).isEqualTo(template.getName());
        assertThat(template1.getVersion()).isEqualTo(template.getVersion());
        assertThat(template1.getDefinition()).isEqualTo(template.getDefinition());

        template.setVersion("2.0.0");
        template2 = createTemplate(template);
        assertThat(template2).isNotNull();
        assertThat(template2.getVersion()).isEqualTo(template.getVersion());

        template.setVersion("3.0.0");
        template3 = createTemplate(template);
        assertThat(template3).isNotNull();
        assertThat(template3.getVersion()).isEqualTo(template.getVersion());

        template.setName("template-2");
        template4 = createTemplate(template);
        assertThat(template4).isNotNull();
        assertThat(template4.getName()).isEqualTo(template.getName());

        ResponseEntity<ExternalComponentResource[]> response = registryClient.getTemplates();
        verifyResponseEntity(response, HttpStatus.OK, true);

        ExternalComponentResource[] definitionResources = response.getBody();
        assertThat(definitionResources.length).isEqualTo(4);

        assertThat(definitionResources[0].getDefinition()).isEqualTo(template.getDefinition());
        assertThat(definitionResources[1].getDefinition()).isEqualTo(template.getDefinition());
        assertThat(definitionResources[2].getDefinition()).isEqualTo(template.getDefinition());
        assertThat(definitionResources[3].getDefinition()).isEqualTo(template.getDefinition());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTemplate() {

        ExternalComponentResource template = null, template1 = null, template2 = null, template3 = null, template4 = null;

        template = resourceBuilder.buildTestTemplate();
        template1 = createTemplate(template);

        template.setVersion("2.0.0");
        template2 = createTemplate(template);

        template.setVersion("3.0.0");
        template3 = createTemplate(template);

        template.setName("template-2");
        template4 = createTemplate(template);

        ResponseEntity<ExternalComponentResource> response = null;

        response = registryClient.getTemplate(template1.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        assertThat(response.getBody()).isEqualTo(template1);

        response = registryClient.getTemplate(template2.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        assertThat(response.getBody()).isEqualTo(template2);

        response = registryClient.getTemplate(template3.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        assertThat(response.getBody()).isEqualTo(template3);

        response = registryClient.getTemplate(template4.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        assertThat(response.getBody()).isEqualTo(template4);

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchTemplates() {

        ExternalComponentResource template = null, template1 = null, template2 = null, template3 = null, template4 = null;

        template = resourceBuilder.buildTestTemplate();
        template1 = createTemplate(template);

        template.setVersion("2.0.0");
        template2 = createTemplate(template);

        template.setVersion("3.0.0");
        template3 = createTemplate(template);

        template.setName("template-2");
        template4 = createTemplate(template);

        Optional<String> name = Optional.ofNullable(null);
        Optional<String> version = Optional.of("3.0.0");
        Optional<String> specification = Optional.ofNullable(null);
        Optional<String> specificationVersion = Optional.ofNullable(null);

        ResponseEntity<ExternalComponentResource[]> getDefinitionResponse = registryClient.searchTemplates(
                name,
                version,
                specification,
                specificationVersion);
        ExternalComponentResource[] definitionResources = getDefinitionResponse.getBody();
        verifyResponseEntity(getDefinitionResponse, HttpStatus.OK, true);

        assertThat(getDefinitionResponse.getBody().length).isEqualTo(2);
        assertThat(definitionResources[0].getDefinition()).isEqualTo(template3.getDefinition());
        assertThat(definitionResources[1].getDefinition()).isEqualTo(template4.getDefinition());

        // TODO try more combination of search parameters and verify response
    }

    // ======================================================================================
    // UPDATE Template
    // ======================================================================================

    // Template are immutable

    // ======================================================================================
    // DELETE Template
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testTemplateBackwardCompatibility() {
        // Test that templates can still be found by their old IDs (backward compatibility)
        
        ExternalComponentResource template = null, createdTemplate = null;
        
        // Create a template with a specific name and version
        template = resourceBuilder.buildTestTemplate();
        createdTemplate = createTemplate(template);
        
        // Verify the template was created with a new random ID
        assertThat(createdTemplate.getId()).isNotNull();
        assertThat(createdTemplate.getId()).isNotEqualTo(template.getId()); // Should be different (random)
        
        // Test backward compatibility: should be able to find template by old ID
        // This tests our implementation that stores oldId and searches by both id and oldId
        ResponseEntity<ExternalComponentResource> response = registryClient.getTemplate(template.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        ExternalComponentResource foundTemplate = response.getBody();
        
        // The found template should be the same as the created one
        assertThat(foundTemplate.getId()).isEqualTo(createdTemplate.getId());
        assertThat(foundTemplate.getName()).isEqualTo(createdTemplate.getName());
        assertThat(foundTemplate.getVersion()).isEqualTo(createdTemplate.getVersion());
        assertThat(foundTemplate.getDefinition()).isEqualTo(createdTemplate.getDefinition());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDeleteTemplate()  {

        ResponseEntity<ExternalComponentResource> response = null;

       ExternalComponentResource template = null, template1 = null, template2 = null, template3 = null, template4 = null;

        template = resourceBuilder.buildTestTemplate();
        template1 = createTemplate(template);

        template.setVersion("2.0.0");
        template2 = createTemplate(template);

        template.setVersion("3.0.0");
        template3 = createTemplate(template);

        template.setName("template-2");
        template4 = createTemplate(template);

        try {
            response = registryClient.deleteOneTemplate(template2.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to delete template: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ExternalComponentResource[] apis;
        try {
            apis = registryClient.readAllTemplates();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read templates: " + t.getMessage());
            return;
        }

        assertThat(apis).isNotNull();
        assertThat(apis.length).isEqualTo(3);

        ResponseEntity<ErrorRes> errorResponse = null;
        try {
            errorResponse = registryClient.getTemplate(template2.getId(), ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible get template: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    // ======================================================================================
    // TEMPLATE SEPARATION TEST
    // ======================================================================================

    /**
     * Test to verify that templates with the same name but different content
     * get different UUIDs and are properly separated.
     * 
     * This test addresses the issue described in:
     * https://github.com/opendatamesh-initiative/odm-platform/issues/274
     * 
     * The problem was that templates with the same name were sharing UUIDs,
     * leading to shared template objects between data product versions.
     * This prevented template updates when the name didn't change and caused
     * sharing across objects with different lifecycles.
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testTemplateSeparationWithDifferentContent() {
        // Step 1: Create first template with specific content
        ExternalComponentResource template1 = resourceBuilder.buildTestTemplate();
        template1.setName("test-task-1");
        template1.setVersion("1.0.0");
        template1.setDefinition("echo 'Hello from task 1'");
        ExternalComponentResource createdTemplate1 = createTemplate(template1);
        
        // Step 2: Create second template with same name but different content
        ExternalComponentResource template2 = resourceBuilder.buildTestTemplate();
        template2.setName("test-task-1");  // Same name
        template2.setVersion("1.0.0");     // Same version
        template2.setDefinition("echo 'Hello from task 2 - different content'");  // Different content
        ExternalComponentResource createdTemplate2 = createTemplate(template2);
        
        // Step 3: Verify that templates have different IDs (random UUIDs)
        assertThat(createdTemplate1.getId()).isNotNull();
        assertThat(createdTemplate2.getId()).isNotNull();
        assertThat(createdTemplate1.getId()).isNotEqualTo(createdTemplate2.getId());
        
        // Step 4: Verify that templates have the same name but different content
        assertThat(createdTemplate1.getName()).isEqualTo("test-task-1");
        assertThat(createdTemplate2.getName()).isEqualTo("test-task-1");
        assertThat(createdTemplate1.getDefinition()).isNotEqualTo(createdTemplate2.getDefinition());
        
        // Step 5: Verify the templates have different content
        assertThat(createdTemplate1.getDefinition()).contains("Hello from task 1");
        assertThat(createdTemplate2.getDefinition()).contains("Hello from task 2 - different content");
        
        // Step 6: Fetch both templates from the registry to verify they exist and are separate
        ResponseEntity<ExternalComponentResource> response1 = registryClient.getTemplate(createdTemplate1.getId());
        ResponseEntity<ExternalComponentResource> response2 = registryClient.getTemplate(createdTemplate2.getId());
        
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ExternalComponentResource fetchedTemplate1 = response1.getBody();
        ExternalComponentResource fetchedTemplate2 = response2.getBody();
        
        // Step 7: Verify the fetched templates are different and correct
        assertThat(fetchedTemplate1.getId()).isEqualTo(createdTemplate1.getId());
        assertThat(fetchedTemplate2.getId()).isEqualTo(createdTemplate2.getId());
        assertThat(fetchedTemplate1.getDefinition()).isEqualTo(createdTemplate1.getDefinition());
        assertThat(fetchedTemplate2.getDefinition()).isEqualTo(createdTemplate2.getDefinition());
    }

    
}