package org.opendatamesh.platform.pp.policy.server.client.notificationservice;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.policy.server.client.utils.RestUtils;
import org.opendatamesh.platform.pp.policy.server.resources.notificationservice.NotificationEventResource;

class NotificationServiceClientImpl implements NotificationServiceClient {

    private static final String route = "api/v1/pp/notification";
    private final String baseUrl;
    private final RestUtils restUtils;

    NotificationServiceClientImpl(String baseUrl, RestUtils restUtils) {
        this.baseUrl = baseUrl;
        this.restUtils = restUtils;
    }

    @Override
    public void notifyEvent(NotificationEventResource eventResource) {
        restUtils.genericPost(String.format("%s/%s/dispatch", baseUrl, route), null, eventResource, JsonNode.class);
    }
}
