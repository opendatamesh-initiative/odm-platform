package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.ApiToSchemaRelationshipResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.api.resources.SchemaResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//TODO every update to data product must check and mock the call to the policyservice

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class SchemaIT extends ODMRegistryIT {


    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Schema
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaCreate() 
    throws IOException {

        SchemaResource schemaRes = createSchema1();

        assertThat(schemaRes.getId()).isNotNull();
        assertThat(schemaRes.getName()).isNotNull();
        assertThat(schemaRes.getVersion()).isNotNull();
    }

    // ----------------------------------------
    // READ Schema
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaReadAll()
            throws IOException {

        createSchema1();

        ResponseEntity<SchemaResource[]> getResponse = registryClient.readSchemas();
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<SchemaResource> schemas = List.of(getResponse.getBody());

        assertThat(schemas.size()).isEqualTo(1);
        assertThat(schemas.get(0).getName()).isNotNull();
        assertThat(schemas.get(0).getVersion()).isNotNull();

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaReadOne()
            throws IOException {

        SchemaResource schema = createSchema1();

        ResponseEntity<SchemaResource> getResponse = registryClient.getSchemaById(schema.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        schema = getResponse.getBody();

        assertThat(schema.getName()).isNotNull();
        assertThat(schema.getVersion()).isNotNull();

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @Disabled // NOT WORKING
    public void testSchemaReadOneContent() throws IOException {

        // TODO: Fix on the controller/service

        SchemaResource schema = createSchema1();

        ResponseEntity<String> getResponse = registryClient.getSchemaContentById(schema.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        String content = getResponse.getBody();

        assertThat(content).isEqualTo(schema.getContent());

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaReadOneRelationships() throws IOException {

        SchemaResource schema = createSchema1();

        ResponseEntity<ApiToSchemaRelationshipResource[]> getResponse = registryClient.getSchemaApiRelationshipById(schema.getId());
        verifyResponseEntity(getResponse, HttpStatus.OK, true);
        List<ApiToSchemaRelationshipResource> relationships = List.of(getResponse.getBody());

        assertThat(relationships.size()).isEqualTo(0);

    }


    // ----------------------------------------
    // DELETE Schema
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaDelete()
            throws IOException {

        SchemaResource schema = createSchema1();

        ResponseEntity<SchemaResource> deleteResponse = registryClient.deleteSchema(schema.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

    }


    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaCreateError400() throws IOException {

        // TEST 1: NULL payload
        ResponseEntity<ErrorRes> postResponse = registryClient.postSchema(null, ErrorRes.class);
        verifyResponseError(
                postResponse,
                HttpStatus.BAD_REQUEST,
                RegistryApiStandardErrors.SC400_12_SCHEMA_IS_EMPTY
        );

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaCreateError422() throws IOException {

        // TEST 1: Emtpy content
        SchemaResource schemaResource = createSchema1();
        String oldContent = schemaResource.getContent();
        schemaResource.setContent(null);
        ResponseEntity<ErrorRes> postResponse = registryClient.postSchema(schemaResource, ErrorRes.class);
        verifyResponseError(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_08_API_NOT_VALID
        );

        // TEST 2: Schema already exists
        schemaResource.setContent(oldContent);
        postResponse = registryClient.postSchema(schemaResource, ErrorRes.class);
        verifyResponseError(
                postResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_11_SCHEMA_ALREADY_EXISTS
        );

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaReadOneError404()
            throws IOException {

        ResponseEntity<ErrorRes> errorResponse = registryClient.getSchemaById(1701l);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_04_SCHEMA_NOT_FOUND
        );

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @Disabled // NOT WORKING
    public void testSchemaReadOneContentError404() throws IOException {

        ResponseEntity<ErrorRes> errorResponse = registryClient.getSchemaContentById(1701l);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_04_SCHEMA_NOT_FOUND
        );

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @Disabled
    public void testSchemaReadOneRelationshipsError404() throws IOException {

        // NOT EXISTING IN CONTROLLER

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaDeleteError404() throws IOException {

        ResponseEntity<ErrorRes> deleteResponse = registryClient.deleteSchema(1701l);
        verifyResponseError(
                deleteResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_04_SCHEMA_NOT_FOUND
        );

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

    // TODO: ...as needed
}