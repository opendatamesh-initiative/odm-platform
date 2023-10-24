package org.opendatamesh.platform.up.executor.api.clients;

import lombok.Data;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

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

        ResponseEntity response =  rest.postForEntity(
                apiUrl(ExecutorAPIRoutes.TASKS),
                getHttpEntity(payload),
                Object.class
        );

        if(response.getStatusCode().is2xxSuccessful()) {
            response = ResponseEntity
                    .status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(mapper.readValue(
                            mapper.writeValueAsString(response.getBody()),
                            responseType
                    ));
            return response;
        } else {
            throw new RuntimeException(response.getBody().toString());
        }
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
