package org.opendatamesh.platform.up.executor.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Data
public class ExecutorClient extends ODMClient {

    public ExecutorClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
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

    public ResponseEntity<TaskResource> postTask(
            Object payload) throws IOException {

        return postTask(payload, TaskResource.class);
    }

    public <T> ResponseEntity<T> postTask(
            Object payload, Class<T> responseType) throws IOException {

        return rest.postForEntity(
                apiUrl(ExecutorAPIRoutes.TASKS),
                getHttpEntity(payload),
                responseType);
    }

    // ----------------------------------------
    // READ
    // ----------------------------------------

    
    public TaskResource readTask(Long id) {

        ResponseEntity<TaskResource> getTaskResponse =  rest.getForEntity(
            apiUrlOfItem(ExecutorAPIRoutes.TASKS),
            TaskResource.class,
            id);

        return getTaskResponse.getBody();
    }
}
