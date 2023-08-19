package org.opendatamesh.platform.pp.devops.api.clients;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

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

        return postActivity(payload, startAfterCreation, ActivityResource.class);
    }

    public <T> ResponseEntity<T> postActivity(
            Object payload, boolean startAfterCreation, Class<T> responseType) throws IOException {

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("startAfterCreation", Boolean.valueOf(startAfterCreation));

        return rest.postForEntity(
                apiUrl(DevOpsAPIRoutes.ACTIVITIES, null, queryParams),
                getHttpEntity(payload),
                responseType,
                queryParams.values().toArray(new Object[0]));
    }

    public ActivityResource[] searchActivities(
            String dataProductId,
            String dataProductVersion,
            String type,
            ActivityStatus status) 
    {
        return getActivities(dataProductId, dataProductVersion, type, status).getBody();
    }

    public ResponseEntity<ActivityResource[]> getActivities(
            String dataProductId,
            String dataProductVersion,
            String type,
            ActivityStatus status)
    {
        return getActivities(dataProductId, dataProductVersion, type, status, ActivityResource[].class);
    }

    public <T> ResponseEntity<T> getActivities(
        String dataProductId,
        String dataProductVersion,
        String type,
        ActivityStatus status,
        Class<T> responseType)
    {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        if (dataProductId != null)
            queryParams.put("dataProductId", dataProductId);
        if (dataProductVersion != null)
            queryParams.put("dataProductVersion", dataProductVersion);
        if (type != null)
            queryParams.put("type", type);
        if (status != null)
            queryParams.put("status", status);

        return rest.getForEntity(
                apiUrl(DevOpsAPIRoutes.ACTIVITIES, queryParams),
                responseType,
                queryParams.values().toArray(new Object[0]));
    }

    public ActivityResource[] readAllActivities() {
        return getActivities().getBody();
    }

    public ResponseEntity<ActivityResource[]> getActivities() {
        return getActivities(ActivityResource[].class);
    }

    public <T> ResponseEntity<T> getActivities(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(DevOpsAPIRoutes.ACTIVITIES),
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
                apiUrlOfItem(DevOpsAPIRoutes.ACTIVITIES),
                responseType,
                activityId.toString());
    }

    public ActivityResource startActivity(Long activityId) throws IOException {
        return patchActivityStart(activityId).getBody();
    }

    public ResponseEntity<ActivityResource> patchActivityStart(Long activityId) throws IOException {
        return patchActivityStart(activityId, ActivityResource.class);
    }

    public <T> ResponseEntity<T> patchActivityStart(Long activityId, Class<T> responseType) throws IOException {
        return patchForEntity(
                apiUrl(DevOpsAPIRoutes.ACTIVITIES, "/{id}/start"),
                HttpEntity.EMPTY,
                responseType,
                activityId);        
    }

    public String readActivityStatus(Long activityId) {
        return getActivityStatus(activityId).getBody();
    }

    public ResponseEntity<String> getActivityStatus(Long activityId) {
        return getActivityStatus(activityId, String.class);
    }

    public <T> ResponseEntity<T> getActivityStatus(Long activityId, Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(DevOpsAPIRoutes.ACTIVITIES, "/{id}/status"),
                responseType,
                activityId.toString());
    }

    // ----------------------------------------
    // Task
    // ----------------------------------------

    public ActivityTaskResource[] searchTasks(
            Long activityId,
            String executorRef,
            ActivityTaskStatus status) {
        return getTasks(activityId, executorRef, status).getBody();
    }

    public ResponseEntity<ActivityTaskResource[]> getTasks(
            Long activityId,
            String executorRef,
            ActivityTaskStatus status) {
        return getTasks(activityId, executorRef, status, ActivityTaskResource[].class);
    }

    public <T> ResponseEntity<T> getTasks(
            Long activityId,
            String executorRef,
            ActivityTaskStatus status,
            Class<T> responseType) {

        Map<String, Object> queryParams = new HashMap<String, Object>();
        if (activityId != null)
            queryParams.put("activityId", activityId);
        if (executorRef != null)
            queryParams.put("executorRef", executorRef);
        if (status != null)
            queryParams.put("status", status);

        return rest.getForEntity(
                apiUrl(DevOpsAPIRoutes.TASKS, queryParams),
                responseType,
                queryParams.values().toArray(new Object[0]));
    }

    public ActivityTaskResource[] readAllTasks() {
        return getTasks().getBody();
    }

    public ResponseEntity<ActivityTaskResource[]> getTasks() {
        return getTasks(ActivityTaskResource[].class);
    }

    public <T> ResponseEntity<T> getTasks(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(DevOpsAPIRoutes.TASKS),
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
                apiUrlOfItem(DevOpsAPIRoutes.TASKS),
                responseType,
                activityId.toString());
    }

    public ActivityTaskResource stopTask(Long taskId) throws IOException {
        return patchTaskStop(taskId).getBody();
    }

    public ResponseEntity<ActivityTaskResource> patchTaskStop(Long taskId) throws IOException {
        return patchTaskStop(taskId, ActivityTaskResource.class);
    }

    public <T> ResponseEntity<T> patchTaskStop(Long taskId, Class<T> responseType) throws IOException {
        return patchForEntity(
                apiUrl(DevOpsAPIRoutes.TASKS, "/{id}/stop"),
                HttpEntity.EMPTY,
                responseType,
                taskId);
    }

    public String readTaskStatus(Long taskId) {
        return getTaskStatus(taskId).getBody();
    }

    public ResponseEntity<String> getTaskStatus(Long taskId) {
        return getTaskStatus(taskId, String.class);
    }

    public <T> ResponseEntity<T> getTaskStatus(Long taskId, Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(DevOpsAPIRoutes.TASKS, "/{id}/status"),
                responseType,
                taskId);
    }

}
