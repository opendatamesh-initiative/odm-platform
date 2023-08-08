package org.opendatamesh.platform.pp.devops.api.clients;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.springframework.http.ResponseEntity;

public class DevOpsClient extends ODMClient {

    public DevOpsClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    // ======================================================================================
    // Proxy services
    // ======================================================================================

    // ----------------------------------------
    // Activity
    // ----------------------------------------

    public ActivityResource createActivity(Object payload, boolean startAfterCreation) throws IOException {
        return postActivity(payload, startAfterCreation).getBody();
    }

    public ResponseEntity<ActivityResource> postActivity(
            Object payload, boolean startAfterCreation) throws IOException {

        return postActivity(payload, startAfterCreation,  ActivityResource.class);
    }

    public <T> ResponseEntity<T> postActivity(
            Object payload, boolean startAfterCreation, Class<T> responseType) throws IOException {

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("startAfterCreation", Boolean.valueOf(startAfterCreation));

        return rest.postForEntity(
                apiUrl(Routes.ACTIVITIES, null, queryParams),
                getHttpEntity(payload),
                responseType,
                queryParams);
    }

    public ActivityResource[] readAllActivities() {
        return getActivities().getBody();
    }


    public ResponseEntity<ActivityResource[]> getActivities() {
        return getActivities(ActivityResource[].class);
    }

    public <T> ResponseEntity<T> getActivities(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(Routes.ACTIVITIES),
                responseType);
    }
    
    public ActivityResource readActivity(Long activityId) {
        return getActivity(activityId).getBody();
    }

    public ResponseEntity<ActivityResource> getActivity(Long activityId) {
        return getActivity(activityId, ActivityResource.class);
    }

    public <T> ResponseEntity<T> getActivity(Long activityId, Class<T> responseType) {
        return rest.getForEntity(
                apiUrlOfItem(Routes.ACTIVITIES),
                responseType,
                activityId.toString());
    }
    
    
    public ActivityResource startActivity(Long activityId) throws IOException {
        return postActivityStart(activityId).getBody();
    }

    public ResponseEntity<ActivityResource> postActivityStart(Long activityId) throws IOException {
        return postActivityStart(activityId, ActivityResource.class);
    }

    public <T> ResponseEntity<T> postActivityStart(Long activityId, Class<T> responseType) throws IOException {
        return rest.postForEntity(
                apiUrl(Routes.ACTIVITIES, "/{id}/start"),
                getHttpEntity(""),
                responseType,
                activityId.toString());
    }


    public String readActivityStatus(Long activityId) {
        return getActivityStatus(activityId).getBody();
    }

    public ResponseEntity<String> getActivityStatus(Long activityId) {
        return getActivityStatus(activityId, String.class);
    }

    public <T> ResponseEntity<T> getActivityStatus(Long activityId, Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(Routes.ACTIVITIES, "/{id}/status"),
                responseType,
                activityId.toString());
    }


    // ----------------------------------------
    // Task
    // ----------------------------------------
    
    public ActivityTaskResource[] readAllTasks() {
        return getTasks().getBody();
    }


    public ResponseEntity<ActivityTaskResource[]> getTasks() {
        return getActivities(ActivityTaskResource[].class);
    }

    public <T> ResponseEntity<T> getTasks(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(Routes.TASKS),
                responseType);
    }


    public ActivityTaskResource readTask(Long taskId) {
        return getTask(taskId).getBody();
    }

    public ResponseEntity<ActivityTaskResource> getTask(Long taskId) {
        return getTask(taskId, ActivityTaskResource.class);
    }

    public <T> ResponseEntity<T> getTask(Long activityId, Class<T> responseType) {
        return rest.getForEntity(
                apiUrlOfItem(Routes.TASKS),
                responseType,
                activityId.toString());
    }

    public ActivityTaskResource stopTask(Long taskId) throws IOException {
        return postTaskStop(taskId).getBody();
    }

    public ResponseEntity<ActivityTaskResource> postTaskStop(Long taskId) throws IOException {
        return postActivityStart(taskId, ActivityTaskResource.class);
    }

    public <T> ResponseEntity<T> postTaskStop(Long taskId, Class<T> responseType) throws IOException {
        return rest.postForEntity(
                apiUrl(Routes.ACTIVITIES, "/{id}/stop"),
                getHttpEntity(""),
                responseType,
                taskId.toString());
    }


    public String readTaskStatus(Long taskId) {
        return getTaskStatus(taskId).getBody();
    }

    public ResponseEntity<String> getTaskStatus(Long activityId) {
        return getActivityStatus(activityId, String.class);
    }

    public <T> ResponseEntity<T> getTaskStatus(Long activityId, Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(Routes.TASKS, "/{id}/status"),
                responseType,
                activityId.toString());
    }

}
