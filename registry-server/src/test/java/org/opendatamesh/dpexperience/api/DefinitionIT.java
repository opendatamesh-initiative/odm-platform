package org.opendatamesh.dpexperience.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.opendatamesh.platform.pp.registry.core.DataProductDescriptor;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InfrastructuralComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.shared.DefinitionResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DefinitionIT extends OpenDataMeshIT {

    InfrastructuralComponentResource infrastructuralComponent;

    ObjectMapper objectMapper;

    @Before
    public void setup() {
        objectMapper = DataProductDescriptor.buildObjectMapper();
    }

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Definition
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionCreate() throws IOException {

        cleanState();

        ResponseEntity<Definition> postProductResponse = null;

        // TEST 1: create a Definition with all properties and verify the response
        Definition definitionRes = createDefinition1();
        assertThat(definitionRes.getId()).isEqualTo(1);
        assertThat(definitionRes.getName()).isEqualTo("definition1");
        assertThat(definitionRes.getVersion()).isEqualTo("0.0.1");
        assertThat(definitionRes.getStatus()).isEqualTo("OK");
        assertThat(definitionRes.getDisplayName()).isEqualTo("testdef1");
        assertThat(definitionRes.getDescription()).isEqualTo("test definition");
        assertThat(definitionRes.getType()).isEqualTo("definition");
        assertThat(definitionRes.getSpecification()).isEqualTo("spec");
        assertThat(definitionRes.getSpecificationVersion()).isEqualTo("1.0");
        assertThat(definitionRes.getContentMediaType()).isEqualTo("plain/text");
        assertThat(definitionRes.getContent()).isEqualTo("Content of test definition");

        // TEST 2: create a Definition without version property and verify the response
        Definition definitionRes2 = createDefinitionMissingVersion();
        assertThat(definitionRes2.getId()).isEqualTo(2);
        assertThat(definitionRes2.getName()).isEqualTo("definition1");
        assertThat(definitionRes2.getVersion()).isEqualTo("1.0.0");
        assertThat(definitionRes2.getStatus()).isEqualTo("OK");
        assertThat(definitionRes2.getDisplayName()).isEqualTo("testdef1");
        assertThat(definitionRes2.getDescription()).isEqualTo("test definition");
        assertThat(definitionRes2.getType()).isEqualTo("definition");
        assertThat(definitionRes2.getSpecification()).isEqualTo("spec");
        assertThat(definitionRes2.getSpecificationVersion()).isEqualTo("1.0");
        assertThat(definitionRes2.getContentMediaType()).isEqualTo("plain/text");
        assertThat(definitionRes2.getContent()).isEqualTo("Content of test definition");

        // TEST 3: create a Definition without name property and verify the response
        Definition definitionRes3 = createDefinitionMissingName();
        assertThat(definitionRes3.getId()).isEqualTo(3);
        assertThat(definitionRes3.getName()).isEqualTo("8d1cd5fa-ec4e-3e5b-b545-a4d2f9cc6753");
        assertThat(definitionRes3.getVersion()).isEqualTo("1.0.1");
        assertThat(definitionRes3.getStatus()).isEqualTo("OK");
        assertThat(definitionRes3.getDisplayName()).isEqualTo("testdef1");
        assertThat(definitionRes3.getDescription()).isEqualTo("test definition");
        assertThat(definitionRes3.getType()).isEqualTo("definition");
        assertThat(definitionRes3.getSpecification()).isEqualTo("spec");
        assertThat(definitionRes3.getSpecificationVersion()).isEqualTo("1.0");
        assertThat(definitionRes3.getContentMediaType()).isEqualTo("plain/text");
        assertThat(definitionRes3.getContent()).isEqualTo("Content of test definition");

        // TEST 4: create a Definition without name and version properties and verify the response
        Definition definitionRes4 = createDefinitionMissingNameAndVersion();
        assertThat(definitionRes4.getId()).isEqualTo(4);
        assertThat(definitionRes4.getName()).isEqualTo("cf9e4b59-af4f-3254-aa44-c7259a7249c9");
        assertThat(definitionRes4.getVersion()).isEqualTo("1.0.0");
        assertThat(definitionRes4.getStatus()).isEqualTo("OK");
        assertThat(definitionRes4.getDisplayName()).isEqualTo("testdef2");
        assertThat(definitionRes4.getDescription()).isEqualTo("test definition 2");
        assertThat(definitionRes4.getType()).isEqualTo("definition");
        assertThat(definitionRes4.getSpecification()).isEqualTo("spec");
        assertThat(definitionRes4.getSpecificationVersion()).isEqualTo("1.0");
        assertThat(definitionRes4.getContentMediaType()).isEqualTo("plain/text");
        assertThat(definitionRes4.getContent()).isEqualTo("Content of test definition 2");

    }

    // ----------------------------------------
    // READ Definition
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionReadAll() throws IOException {

        cleanState();

        createDefinition1();
        createDefinitionMissingVersion();
        createDefinitionMissingName();
        createDefinitionMissingNameAndVersion();

        ResponseEntity<DefinitionResource[]> getDefinitionResponse = rest.readAllDefinitions();
        DefinitionResource[] definitionResources = getDefinitionResponse.getBody();
        verifyResponseEntity(getDefinitionResponse, HttpStatus.OK, true);

        assertThat(getDefinitionResponse.getBody().length).isEqualTo(4);
        assertThat(definitionResources[0].getContent()).isEqualTo("Content of test definition");
        assertThat(definitionResources[1].getContent()).isEqualTo("Content of test definition");
        assertThat(definitionResources[2].getContent()).isEqualTo("Content of test definition");
        assertThat(definitionResources[3].getContent()).isEqualTo("Content of test definition 2");

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionSearch() throws IOException {

        cleanState();

        createDefinition1();
        createDefinitionMissingVersion();
        createDefinitionMissingName();
        createDefinitionMissingNameAndVersion();

        Optional<String> name = Optional.ofNullable(null);
        Optional<String> version = Optional.of("1.0.0");
        Optional<String> type = Optional.ofNullable(null);
        Optional<String> specification = Optional.ofNullable(null);
        Optional<String> specificationVersion = Optional.ofNullable(null);

        ResponseEntity<DefinitionResource[]> getDefinitionResponse = rest.searchDefinitions(
                name,
                version,
                type,
                specification,
                specificationVersion);
        DefinitionResource[] definitionResources = getDefinitionResponse.getBody();
        verifyResponseEntity(getDefinitionResponse, HttpStatus.OK, true);

        assertThat(getDefinitionResponse.getBody().length).isEqualTo(2);
        assertThat(definitionResources[0].getContent()).isEqualTo("Content of test definition");
        assertThat(definitionResources[1].getContent()).isEqualTo("Content of test definition 2");

        // TODO try more combination of search parameters and verify response
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionReadOne() throws IOException {

        cleanState();

        Definition definitionResource = createDefinition1();

        ResponseEntity<DefinitionResource> getDefinitionResponse = rest.readOneDefinition(definitionResource.getId());
        DefinitionResource definitionRes = getDefinitionResponse.getBody();

        verifyResponseEntity(getDefinitionResponse, HttpStatus.OK, true);
        assertThat(definitionRes.getId()).isEqualTo(1);
        assertThat(definitionRes.getName()).isEqualTo("definition1");
        assertThat(definitionRes.getVersion()).isEqualTo("0.0.1");
        assertThat(definitionRes.getStatus()).isEqualTo("OK");
        assertThat(definitionRes.getDisplayName()).isEqualTo("testdef1");
        assertThat(definitionRes.getDescription()).isEqualTo("test definition");
        assertThat(definitionRes.getType()).isEqualTo("definition");
        assertThat(definitionRes.getSpecification()).isEqualTo("spec");
        assertThat(definitionRes.getSpecificationVersion()).isEqualTo("1.0");
        assertThat(definitionRes.getContentMediaType()).isEqualTo("plain/text");
        assertThat(definitionRes.getContent()).isEqualTo("Content of test definition");

    }

    // ----------------------------------------
    // UPDATE Definition
    // ----------------------------------------

    // Definitions are immutable

    // ----------------------------------------
    // DELETE Definition
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionDelete() throws IOException {

        cleanState();

        Definition definitionResource = createDefinition1();

        ResponseEntity<Void> getDefinitionResponse = rest.deleteDefinition(definitionResource.getId());
        verifyResponseEntity(getDefinitionResponse, HttpStatus.OK, false);

    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Definition
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionCreationError400Errors() throws IOException {

        cleanState();

        HttpEntity<DefinitionResource> entity = null;
        ResponseEntity<ErrorRes> errorResponse = null;

        // Test error SC400_09_STDDEF_ID_IS_EMPTY
        entity = rest.getDefinitionAsHttpEntity(null);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DEFINITIONS),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.BAD_REQUEST, OpenDataMeshAPIStandardError.SC400_08_STDDEF_IS_EMPTY);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionCreateError404Errors() {
        // TODO - exists?
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionCreateError422Errors() throws IOException {

        cleanState();

        HttpEntity<DefinitionResource> entity = null;
        ResponseEntity<ErrorRes> errorResponse = null;

        createDefinition1();

        // TEST 1: try to register the same definition again
        entity = rest.getDefinitionAsHttpEntity(RESOURCE_DEF1_V1);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DEFINITIONS),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_06_STDDEF_ALREADY_EXISTS);

        // TEST 2: try to register a definition without setting the content
        entity.getBody().setContent(null);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DEFINITIONS),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_08_DEFINITION_DOC_SYNTAX_IS_INVALID);

    }

    // ----------------------------------------
    // DELETE Definition
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionDeleteError400Errors() {
        // TODO - exists?
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionDeleteError404Errors() {

        cleanState();

        ResponseEntity<ErrorRes> errorResponse = null;

        errorResponse = rest.exchange(
                apiUrlOfItem(RoutesV1.DEFINITIONS),
                HttpMethod.DELETE,
                null,
                ErrorRes.class,
                "1"
        );

        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                OpenDataMeshAPIStandardError.SC404_03_STDDEF_NOT_FOUND);

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionDeleteError422Errors() {
        // TODO - exists?
    }

    // ----------------------------------------
    // OTHER ...
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionMediaTypeErrors() {
        // TODO test the acceptable media types for create endpoint - exists?
        // TODO test one wrong media type for create endpoint - exists?
    }

    // ======================================================================================
    // PRIVATE METHODS
    // ======================================================================================

    // ----------------------------------------
    // Create test resources
    // ----------------------------------------

    // TODO: ...as needed

    // ----------------------------------------
    // Verify test resources
    // ----------------------------------------

    // ----------------------------------------
    // Clean state for each test: empty DB
    // ----------------------------------------
    private void cleanState() {

        ResponseEntity<DefinitionResource[]> getDefinitionResponse = rest.readAllDefinitions();
        DefinitionResource[] definitionResources = getDefinitionResponse.getBody();
        for (DefinitionResource definitionResource : definitionResources) {
            rest.deleteDefinition(definitionResource.getId());
        }

    }

}