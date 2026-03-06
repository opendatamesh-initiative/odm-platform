package org.opendatamesh.platform.pp.notification.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.notification.api.resources.v2.EventV2SubscribeResponseResource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * V2-only notification client for observer subscription (subscribe endpoint).
 * Accepts both 200 OK and 201 Created as success.
 */
public class NotificationClientV2Impl {

    private static final String routeV2 = "api/v2/pp/notification";

    private final String baseUrl;
    private final TestRestTemplate rest;
    private final ObjectMapper objectMapper;

    public NotificationClientV2Impl(String serverAddress) {
        ODMClient odmClient = new ODMClient(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        this.baseUrl = normalizeBaseUrl(serverAddress);
        this.rest = odmClient.rest;
        this.objectMapper = ObjectMapperFactory.JSON_MAPPER;
    }

    private static String normalizeBaseUrl(String url) {
        if (url == null) return null;
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public EventV2SubscribeResponseResource subscribeObserverV2(
            EventV2SubscribeResponseResource.EventV2SubscribeResource observerSubscribeResource) {
        String url = String.format("%s/%s/subscriptions/subscribe", baseUrl, routeV2);
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(observerSubscribeResource),
                    ObjectNode.class
            );
            HttpStatus status = responseEntity.getStatusCode();
            if (status == HttpStatus.OK || status == HttpStatus.CREATED) {
                return objectMapper.treeToValue(responseEntity.getBody(), EventV2SubscribeResponseResource.class);
            }
            throw new InternalServerException("Subscribe failed with status: " + status);
        } catch (RestClientException | JsonProcessingException e) {
            throw new InternalServerException(e);
        }
    }
}
