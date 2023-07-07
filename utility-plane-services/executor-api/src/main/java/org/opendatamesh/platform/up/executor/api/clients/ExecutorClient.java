package org.opendatamesh.platform.up.executor.api.clients;

import org.springframework.http.*;
import java.util.Collections;
import java.util.Objects;


import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.springframework.web.client.RestTemplate;

public class ExecutorClient {

    String address;
    RestTemplate restTemplate;

    public ExecutorClient(String address) {
        this.address = Objects.requireNonNull(address);
        restTemplate = new RestTemplate();
    }

    public TaskResource createTask(TaskResource task) {

        HttpEntity<TaskResource> entity = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

       
        entity = new HttpEntity<TaskResource>(task, headers);

        ResponseEntity<TaskResource> postTaskResponse = restTemplate.postForEntity(
            apiUrl(Routes.TASKS),
            entity,
            TaskResource.class);

        return postTaskResponse.getBody();
    }

    public TaskResource readTask(Long id) {

        ResponseEntity<TaskResource> getTaskResponse =  restTemplate.getForEntity(
            apiUrlOfItem(Routes.TASKS),
            TaskResource.class,
            id);

        return getTaskResponse.getBody();
    }

    protected String apiUrl(Routes route) {
        return apiUrl(route, "");
    }

    protected String apiUrl(Routes route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    protected String apiUrlOfItem(Routes route) {
        return apiUrl(route, "/{id}");
    }

    protected String apiUrlFromString(String routeUrlString) {
        return address + routeUrlString;
    }

    private static enum Routes {

        TASKS("/api/v1/up/executor/tasks");

        private final String path;

        private Routes(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return this.path;
        }

        public String getPath() {
            return path;
        }
    }

}
