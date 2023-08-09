package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.registry.api.v1.resources.SchemaResource;
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
    public void testSchemaReadOneError404()
            throws IOException {

        ResponseEntity<SchemaResource> getResponse = registryClient.getSchemaById(1701l);
        verifyResponseEntity(getResponse, HttpStatus.NOT_FOUND, false);

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSchemaDeleteError404()
            throws IOException {

        createSchema1();

        ResponseEntity<SchemaResource> deleteResponse = registryClient.deleteSchema(1701l);
        verifyResponseEntity(deleteResponse, HttpStatus.NOT_FOUND, false);

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