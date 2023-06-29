package org.opendatamesh.platform.pp.registry;

import org.junit.Before;
import org.junit.Test;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.TemplateResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TemplateIT extends OpenDataMeshIT {

    @Before
    public void setup() {
        //objectMapper = DataProductDescriptor.buildObjectMapper();
    }

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Template
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateCreate() throws IOException {

        cleanState();

        // TEST 1: create a Template with all properties and verify the response
        TemplateResource templateRes = createTemplate1();
        assertThat(templateRes.getId()).isEqualTo(1);
        assertThat(templateRes.getDescription()).isEqualTo("Github Template");
        assertThat(templateRes.getMediaType()).isEqualTo("text");
        assertThat(templateRes.getHref()).isEqualTo("https://github.com/Giandom/tf-data-product-example.git");

        // TEST 2: create a Template without version property and verify the response
        // ERROR SCENARIO

        // TEST 3: create a Template without name property and verify the response
        // ERROR SCENARIO

        // TEST 4: create a Template without name and version properties and verify the response
        // ERROR SCENARIO

    }

    // ----------------------------------------
    // READ Template
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateReadAll() throws IOException {

        cleanState();

        createTemplate1();
        createTemplate2();

        ResponseEntity<TemplateResource[]> getTemplateResponse = rest.readAllTemplates();
        TemplateResource[] templateResources = getTemplateResponse.getBody();
        verifyResponseEntity(getTemplateResponse, HttpStatus.OK, true);

        assertThat(getTemplateResponse.getBody().length).isEqualTo(2);
        assertThat(templateResources[0].getDescription()).isEqualTo("Github Template");
        assertThat(templateResources[1].getDescription()).isEqualTo("Github Template - 2");

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateSearch() throws IOException {

        cleanState();

        createTemplate1();
        createTemplate2();

        // TEST 1: search by Media Type

        Optional<String> mediaType = Optional.of("text");

        ResponseEntity<TemplateResource[]> getTemplateResponse = rest.searchTemplates(
                mediaType
        );
        TemplateResource[] templateResources = getTemplateResponse.getBody();
        verifyResponseEntity(getTemplateResponse, HttpStatus.OK, true);

        assertThat(getTemplateResponse.getBody().length).isEqualTo(1);
        assertThat(templateResources[0].getDescription()).isEqualTo("Github Template");

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateReadOne() throws IOException {

        cleanState();

        TemplateResource templateResource = createTemplate1();

        ResponseEntity<TemplateResource> getTemplateResponse = rest.readOneTemplate(templateResource.getId());
        TemplateResource templateRes = getTemplateResponse.getBody();

        verifyResponseEntity(getTemplateResponse, HttpStatus.OK, true);
        assertThat(templateRes.getId()).isEqualTo(1);
        assertThat(templateRes.getDescription()).isEqualTo("Github Template");
        assertThat(templateRes.getMediaType()).isEqualTo("text");
        assertThat(templateRes.getHref()).isEqualTo("https://github.com/Giandom/tf-data-product-example.git");

    }

    // ----------------------------------------
    // UPDATE Template
    // ----------------------------------------

    // Templates are immutable

    // ----------------------------------------
    // DELETE Template
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateDelete() throws IOException {

        cleanState();

        TemplateResource templateResource = createTemplate1();

        ResponseEntity<Void> getTemplateResponse = rest.deleteTemplate(templateResource.getId());
        verifyResponseEntity(getTemplateResponse, HttpStatus.OK, false);

    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Template
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateCreationError400Errors() throws IOException {

        cleanState();

        HttpEntity<TemplateResource> entity = null;
        ResponseEntity<ErrorRes> errorResponse = null;

        // Test error SC400_12_TEMPLATE_IS_EMPTY
        entity = rest.getTemplateAsHttpEntity(null);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.TEMPLATES),
                entity,
                ErrorRes.class
        );
        verifyResponseError(errorResponse,
                HttpStatus.BAD_REQUEST, OpenDataMeshAPIStandardError.SC400_14_TEMPLATE_IS_EMPTY);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateCreateError422Errors() throws IOException {

        cleanState();

        HttpEntity<TemplateResource> entity = null;
        ResponseEntity<ErrorRes> errorResponse = null;

        createTemplate1();

        // TEST 1: try to register the same template again
        entity = rest.getTemplateAsHttpEntity(RESOURCE_TEMPLATE_1);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.TEMPLATES),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_13_TEMPLATE_ALREADY_EXISTS);

        // TEST 2: try to register a template without setting the href
        entity.getBody().setHref(null);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.TEMPLATES),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_14_TEMPLATE_DOC_SYNTAX_IS_INVALID);

        // TEST 3: try to register a template without setting the mediaType
        entity.getBody().setMediaType(null);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.TEMPLATES),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_14_TEMPLATE_DOC_SYNTAX_IS_INVALID);

    }


    // ----------------------------------------
    // READ Template
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateReadOne404Error() throws IOException {

        cleanState();

        ResponseEntity<ErrorRes> errorResponse = null;

        errorResponse = rest.getForEntity(
                apiUrlOfItem(RoutesV1.TEMPLATES),
                ErrorRes.class,
                1
        );

        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                OpenDataMeshAPIStandardError.SC404_05_TEMPLATE_NOT_FOUND
        );

    }


    // ----------------------------------------
    // DELETE Template
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testTemplateDeleteError404Errors() {

        cleanState();

        ResponseEntity<ErrorRes> errorResponse = null;

        errorResponse = rest.exchange(
                apiUrlOfItem(RoutesV1.TEMPLATES),
                HttpMethod.DELETE,
                null,
                ErrorRes.class,
                "1"
        );

        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                OpenDataMeshAPIStandardError.SC404_05_TEMPLATE_NOT_FOUND
        );

    }


    // ----------------------------------------
    // UTILS
    // ----------------------------------------

    // ----------------------------------------
    // Clean state for each test: empty DB
    // ----------------------------------------
    private void cleanState() {

        ResponseEntity<TemplateResource[]> getTemplateResponse = rest.readAllTemplates();
        TemplateResource[] templateResources = getTemplateResponse.getBody();
        for (TemplateResource templateResource : templateResources) {
            rest.deleteTemplate(templateResource.getId());
        }

    }
    
}
