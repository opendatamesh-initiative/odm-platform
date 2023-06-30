package org.opendatamesh.platform.pp.registry;

import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

@TestPropertySource(properties = { "spring.test.context.parallel.enabled=false" })
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class UploadIT extends OpenDataMeshIT {
    
    @Autowired
    ObjectMapper objectMapper;
    

    @Before
    public void setup() {
        //objectMapper = DataProductDescriptor.buildObjectMapper();
    }

    // ----------------------------------------
    // CREATE Data product version
    // ----------------------------------------
    /*@Test
    @Order(1)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionCreation() throws IOException {
        String descriptorContent = uploadDataProductVersion();
        verifyBasicContent(descriptorContent);
    }

    private void verifyBasicContent(String descriptorContent) {

    }*/ //ERRORS
}
