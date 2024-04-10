package org.opendatamesh.platform.pp.event.notifier.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.utils.RestUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ObserverSearchOptions;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class EventNotifierClientImpl extends ODMClient implements EventNotifierClient {

    private final RestUtils restUtils;

    public EventNotifierClientImpl(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }

    public EventNotifierClientImpl(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
        restUtils = new RestUtils(rest, ObjectMapperFactory.JSON_MAPPER);
    }

    public ObserverResource addObserver(ObserverResource observerResource) {
        return restUtils.create(apiUrl(EventNotifierAPIRoutes.OBSERVERS), observerResource, ObserverResource.class);
    }

    public ObserverResource updateObserver(Long id, ObserverResource observerResource) {
        return restUtils.put(apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS), id, observerResource, ObserverResource.class);
    }

    public Page<ObserverResource> getObservers(Pageable pageable, ObserverSearchOptions searchOptions) {
        return restUtils.getPage(apiUrl(EventNotifierAPIRoutes.OBSERVERS), pageable, searchOptions, ObserverResource.class);
    }

    public ObserverResource getObserver(Long id) {
        return restUtils.get(apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS), id, ObserverResource.class);
    }

    public void removeObserver(Long id) {
        restUtils.delete(apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS), id);
    }

    public void notifyEvent(EventResource eventResource) {
        restUtils.genericPost(apiUrl(EventNotifierAPIRoutes.OBSERVERS), eventResource, EventResource.class);
    }

    public ResponseEntity<ObjectNode> addObserverResponseEntity(ObserverResource observerResource) {
        return rest.exchange(
                apiUrl(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.POST,
                new HttpEntity<>(observerResource),
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> updateObserverResponseEntity(Long id, ObserverResource observerResource) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.PUT,
                new HttpEntity<>(observerResource),
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> getObserversResponseEntity() {
        return rest.exchange(
                apiUrl(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.GET,
                null,
                ObjectNode.class
        );
    }

    public ResponseEntity<ObjectNode> getObserverResponseEntity(Long id) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.GET,
                null,
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> removeObserverResponseEntity(Long id) {
        return rest.exchange(
                apiUrlOfItem(EventNotifierAPIRoutes.OBSERVERS),
                HttpMethod.DELETE,
                null,
                ObjectNode.class,
                id
        );
    }

    public ResponseEntity<ObjectNode> notifyEventResponseEntity(EventResource eventResource) {
        return rest.exchange(
                apiUrl(EventNotifierAPIRoutes.DISPATCH),
                HttpMethod.POST,
                new HttpEntity<>(eventResource),
                ObjectNode.class
        );
    }

}
