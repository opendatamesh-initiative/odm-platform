package org.opendatamesh.platform.pp.devops.server.clients;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExecutorClientWithSecrets extends ODMClient {

    private Boolean checkAfterCallback;
    private Map<String, String> secretHeaders;

    public ExecutorClientWithSecrets(String serverAddress, Boolean checkAfterCallback) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        this.checkAfterCallback = checkAfterCallback;
        this.secretHeaders = new HashMap<>();
    }

    public ExecutorClientWithSecrets(String serverAddress, Boolean checkAfterCallback, Map<String, String> secretHeaders) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        this.checkAfterCallback = checkAfterCallback;
        this.secretHeaders = secretHeaders != null ? secretHeaders : new HashMap<>();
    }

    // ======================================================================================
    // TASK
    // ======================================================================================

    // ----------------------------------------
    // CREATE
    // ----------------------------------------

    public TaskResource createTask(Object payload) throws IOException {
        return postTask(payload).getBody();
    }

    public ResponseEntity<TaskResource> postTask(Object payload) throws IOException {
        return postTask(payload, TaskResource.class);
    }

    public <T> ResponseEntity<T> postTask(Object payload, Class<T> responseType) throws IOException {
        HttpEntity<Object> httpEntity = getHttpEntityWithSecrets(payload);
        
        ResponseEntity<Object> response = rest.postForEntity(
                apiUrl(ExecutorAPIRoutes.TASKS),
                httpEntity,
                Object.class
        );

        if(response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(mapper.readValue(
                            mapper.writeValueAsString(response.getBody()),
                            responseType
                    ));
        } else {
            throw new RuntimeException(response.getBody().toString());
        }
    }

    // ----------------------------------------
    // READ
    // ----------------------------------------
    
    public TaskResource readTask(Long id) {
        HttpHeaders headers = getHeadersWithSecrets();
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        
        ResponseEntity<TaskResource> getTaskResponse = rest.exchange(
            apiUrlOfItem(ExecutorAPIRoutes.TASKS),
            org.springframework.http.HttpMethod.GET,
            httpEntity,
            TaskResource.class,
            id
        );

        return getTaskResponse.getBody();
    }

    public TaskStatus readTaskStatus(Long id) {
        HttpHeaders headers = getHeadersWithSecrets();
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        
        ResponseEntity<TaskStatus> getTaskStatusResponse = rest.exchange(
                apiUrl(ExecutorAPIRoutes.TASK_STATUS),
                org.springframework.http.HttpMethod.GET,
                httpEntity,
                TaskStatus.class,
                id
        );

        if(getTaskStatusResponse.getStatusCode().is2xxSuccessful())
            return getTaskStatusResponse.getBody();
        else
            throw new RuntimeException(getTaskStatusResponse.getBody().toString());
    }

    // ======================================================================================
    // HELPER METHODS FOR HEADER HANDLING
    // ======================================================================================

    /**
     * Creates HttpEntity with secret headers included
     */
    private <T> HttpEntity<T> getHttpEntityWithSecrets(T payload) throws IOException {
        HttpHeaders headers = getHeadersWithSecrets();
        return new HttpEntity<T>(payload, headers);
    }

    /**
     * Gets headers with secret headers included
     */
    private HttpHeaders getHeadersWithSecrets() {
        HttpHeaders headers = getHeaders();
        
        // Add secret headers if they exist
        if (secretHeaders != null && !secretHeaders.isEmpty()) {
            for (Map.Entry<String, String> secretEntry : secretHeaders.entrySet()) {
                headers.set(secretEntry.getKey(), secretEntry.getValue());
            }
        }
        
        return headers;
    }

    // ======================================================================================
    // INNER CLASSES
    // ======================================================================================

    /**
     * ExecutorAPIRoutes enum - copied from the original ExecutorClient
     */
    public enum ExecutorAPIRoutes implements org.opendatamesh.platform.core.commons.clients.ODMApiRoutes {
       
        TASKS("/tasks"),
        TASK_STATUS("/tasks/{id}/status");

        private final String path;
        private static final String CONTEXT_PATH = "/api/v1/up/executor";

        ExecutorAPIRoutes(String path) {
            this.path = CONTEXT_PATH + path;
        }

        @Override
        public String getPath() {
            return path;
        }
    }
}
