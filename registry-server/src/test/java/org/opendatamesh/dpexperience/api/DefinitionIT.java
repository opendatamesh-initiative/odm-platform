package org.opendatamesh.dpexperience.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.opendatamesh.platform.pp.registry.core.DataProductDescriptor;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InfrastructuralComponentResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    public void testDefinitionCreate() {
        // TODO create a Definition with all properties and verify the response
        // TODO create a Definition without version property and verify the response
        // TODO create a Definition without name property and verify the response
        // TODO create a Definition without name and version properties and verify the
        // response
    }

    // ----------------------------------------
    // READ Definition
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductReadAll() {
        // TODO read all definitions and verify response

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionSearch() {
        // TODO read definitions applying different filter combinantion and verify
        // response
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductReadOne() {
        // TODO read one definition and verify response
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
    public void testDataProductDelete() {
        // TODO
    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Definition
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionCreationError400Errors() {
        // TODO
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreateError404Errors() {
        // TODO
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreateError422Errors() {
        // TODO
    }

    // ----------------------------------------
    // DELETE Definition
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionDeleteError400Errors() {
        // TODO
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductDeleteError404Errors() {
        // TODO
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductDeleteError422Errors() {
        // TODO
    }

    // ----------------------------------------
    // OTHER ...
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDefinitionMediaTypeErrors() {
        // TODO test the acceptable media types for create and update endpoints
        // TODO test one wrong media type for create and update endpoints
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

}