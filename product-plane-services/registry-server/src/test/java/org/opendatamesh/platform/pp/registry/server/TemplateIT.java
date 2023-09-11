package org.opendatamesh.platform.pp.registry.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class TemplateIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Api
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateTemplateWithAllProperties() {

        ExternalComponentResource template = null, template1 = null;

        template = resourceBuilder.buildTestTemplate();

        template1 = createTemplate(template);

        assertThat(template1.getId()).isEqualTo(template.getId());
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

        String descriptorContent = createDataProductVersion(createdDataProductRes.getId(), ODMRegistryResources.DPD_CORE_PROPS_CUSTOM);
        DataProductVersionDPDS dataProductVersion = null;
        System.out.println(descriptorContent);
        
        DPDSParser parser = new DPDSParser();
        DescriptorLocation location = new UriLocation(descriptorContent);
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost");
        options.setRewriteEntityType(false);
        try {
            dataProductVersion = parser.parse(location, options).getDescriptorDocument();
        } catch (Throwable t) {
            fail("Impossible to parse descriptor content", t);
        }

        LifecycleInfoDPDS lifecycleInfo = dataProductVersion.getInternalComponents().getLifecycleInfo();
        LifecycleActivityInfoDPDS activity = lifecycleInfo.getActivityInfo("test");
        StandardDefinitionDPDS templateStdDef = activity.getTemplate();
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
        assertThat(template1).isEqualTo(template);

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

    
}