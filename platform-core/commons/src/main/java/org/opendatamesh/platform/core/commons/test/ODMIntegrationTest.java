package org.opendatamesh.platform.core.commons.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.api.AssertionsForClassTypes;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public abstract class ODMIntegrationTest {

    protected ODMResourceBuilder resourceBuilder;

    protected ObjectMapper mapper;
    
    // ======================================================================================
    // Verify test basic resources
    // ======================================================================================

    protected ResponseEntity verifyResponseEntity(
            ResponseEntity responseEntity, HttpStatus statusCode, boolean checkBody
    ) {
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(statusCode);
        if (checkBody)
            AssertionsForClassTypes.assertThat(responseEntity.getBody()).isNotNull();
        return responseEntity;
    }

    protected void verifyResponseError(
            ResponseEntity<ErrorRes> errorResponse,
            HttpStatus status,
            ODMApiStandardErrors error
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
    }

    protected void verifyResponseError(
            ResponseEntity<ErrorRes> errorResponse,
            HttpStatus status,
            ODMApiStandardErrors error,
            String message
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getMessage()).isEqualTo(message);
    }

    protected void verifyResponseErrorObjectNode(
            ResponseEntity<ObjectNode> errorResponse,
            HttpStatus status,
            ODMApiStandardErrors error
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        ErrorRes errorRes = mapper.convertValue(errorResponse.getBody(), ErrorRes.class);
        AssertionsForClassTypes.assertThat(errorRes.getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorRes.getDescription()).isEqualTo(error.description());
    }

    protected void verifyResponseErrorObjectNode(
            ResponseEntity<ObjectNode> errorResponse,
            HttpStatus status,
            ODMApiStandardErrors error,
            String message
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        ErrorRes errorRes = mapper.convertValue(errorResponse.getBody(), ErrorRes.class);
        AssertionsForClassTypes.assertThat(errorRes.getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorRes.getDescription()).isEqualTo(error.description());
        AssertionsForClassTypes.assertThat(errorRes.getMessage()).isEqualTo(message);
    }


    // ======================================================================================
    // Utils
    // ======================================================================================

    protected String[] truncateAllTablesFromDb(JdbcTemplate jdbcTemplate, File tableSetFile) throws IOException {
        String[] tableSet = Files.readAllLines(tableSetFile.toPath(), Charset.defaultCharset()).toArray(new String[0]);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, tableSet);
        return tableSet;
    }

    protected <T> List<T> extractListFromPageFromObjectNode(ObjectNode content, Class<T> targetClassType) {
        Page<?> page = mapper.convertValue(content, Page.class);
        return page.getContent().stream()
                .map(record -> mapper.convertValue(record, targetClassType))
                .collect(Collectors.toList());
    }
   
}
