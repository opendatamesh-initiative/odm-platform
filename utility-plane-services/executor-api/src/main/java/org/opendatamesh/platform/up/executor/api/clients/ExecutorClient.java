package org.opendatamesh.platform.up.executor.api.clients;

import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

import java.util.Collections;
import java.util.Objects;

@Data
public class ExecutorClient {

    String address;
    RestTemplate rest;

    public ExecutorClient(String address) {
        this.address = Objects.requireNonNull(address);
        rest = new RestTemplate();
    }

    public TaskResource createTask(TaskResource task) {

        HttpEntity<TaskResource> entity = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

       
        entity = new HttpEntity<TaskResource>(task, headers);

        ResponseEntity<TaskResource> postTaskResponse = rest.postForEntity(
            apiUrl(ExecutorAPIRoutes.TASKS),
            entity,
            TaskResource.class);

        return postTaskResponse.getBody();
    }

    public TaskResource readTask(Long id) {

        ResponseEntity<TaskResource> getTaskResponse =  rest.getForEntity(
            apiUrlOfItem(ExecutorAPIRoutes.TASKS),
            TaskResource.class,
            id);

        return getTaskResponse.getBody();
    }

    public String apiUrl(ExecutorAPIRoutes route) {
        return apiUrl(route, "");
    }

    public String apiUrl(ExecutorAPIRoutes route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    public String apiUrlOfItem(ExecutorAPIRoutes route) {
        return apiUrl(route, "/{id}");
    }

    public String apiUrlFromString(String routeUrlString) {
        return address + routeUrlString;
    }
}
