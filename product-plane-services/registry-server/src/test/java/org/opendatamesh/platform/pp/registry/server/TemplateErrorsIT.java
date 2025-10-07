package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryAPIRoutes;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TemplateErrorsIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Template
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateTemplateWithMissingPayload() {

        ResponseEntity<ErrorRes> response = null;
        try {
            response = registryClient.postTemplate(null, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post template definition: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC400_14_TEMPLATE_IS_EMPTY.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC400_14_TEMPLATE_IS_EMPTY.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.TEMPLATES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateTemplateWithEmptyPayload() {

        ResponseEntity<ErrorRes> response = null;
        try {
            response = registryClient.postTemplate("    ", ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post template: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(ODMApiCommonErrors.SC400_00_REQUEST_BODY_IS_NOT_READABLE.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMApiCommonErrors.SC400_00_REQUEST_BODY_IS_NOT_READABLE.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.TEMPLATES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateTemplateWithMissingName() throws IOException {

        ResponseEntity<ErrorRes> response = null;

        ExternalComponentResource template = resourceBuilder.buildTestTemplate();
        template.setName(null);

        try {
            response = registryClient.postTemplate(template, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post template: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.TEMPLATES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }


    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateTemplateWithMissingVersion() throws IOException {

        ResponseEntity<ErrorRes> response = null;

        ExternalComponentResource template = resourceBuilder.buildTestTemplate();
        template.setVersion(null);

        try {
            response = registryClient.postTemplate(template, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post template: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.TEMPLATES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateTemplateWithMissingDefinition() throws IOException {

        ResponseEntity<ErrorRes> response = null;

        ExternalComponentResource template = resourceBuilder.buildTestTemplate();
        template.setDefinition(null);

        try {
            response = registryClient.postTemplate(template, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post template: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.TEMPLATES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }
   
    // ----------------------------------------
    // DELETE Definition
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDeleteNotExistingTemplate() {
        ResponseEntity<ErrorRes> response = null;

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
            response = registryClient.deleteTemplate("wrong-id", ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to delete template: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC404_05_TEMPLATE_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC404_05_TEMPLATE_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.TEMPLATES.getPath() + "/wrong-id");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }
    
}
