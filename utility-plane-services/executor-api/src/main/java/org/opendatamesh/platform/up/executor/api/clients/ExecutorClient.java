package org.opendatamesh.platform.up.executor.api.clients;

import lombok.Data;
import org.apache.xpath.operations.Bool;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@Data
public class ExecutorClient extends ODMClient {

    private Boolean checkAfterCallback;

    public ExecutorClient(String serverAddress, Boolean checkAfterCallback) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        this.checkAfterCallback = checkAfterCallback;
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

    public TaskStatus readTaskStatus(Long id) {

        ResponseEntity<TaskStatus> getTaskStatusResponse = rest.getForEntity(
                apiUrl(ExecutorAPIRoutes.TASK_STATUS),
                TaskStatus.class,
                id
        );

        if(getTaskStatusResponse.getStatusCode().is2xxSuccessful())
            return getTaskStatusResponse.getBody();
        else
            throw new RuntimeException(getTaskStatusResponse.getBody().toString());

    }

}
