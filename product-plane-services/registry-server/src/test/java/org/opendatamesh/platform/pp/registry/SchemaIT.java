package org.opendatamesh.platform.pp.registry;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;

//TODO every update to data product must check and mock the call to the policyservice

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class SchemaIT extends OpenDataMeshIT {


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

        //SchemaResource schemaRes = createSchema1();

        // TEST 1: create first data product
        //assertThat(schemaRes.getId()).isNotNull();
        //assertThat(schemaRes.getName()).isNotNull();
        //assertThat(schemaRes.getVersion()).isNotNull();
    }

    

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

   
   

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