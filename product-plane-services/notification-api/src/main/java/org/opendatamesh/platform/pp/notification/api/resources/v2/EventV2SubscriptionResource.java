package org.opendatamesh.platform.pp.notification.api.resources.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.core.commons.resources.utils.TimestampedResource;
import org.opendatamesh.platform.pp.notification.api.resources.v2.enums.EventV2NotificationStatus;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventV2SubscriptionResource extends TimestampedResource implements Cloneable {

    @Schema(description = "Unique identifier (UUID) of the subscription", example = "6e1b2a41-2f24-4b56-8a3f-2149f1d456b7")
    private String uuid;

    @Schema(description = "Internal name of the subscription")
    private String name;

    @Schema(description = "Human-readable display name of the subscription")
    private String displayName;

    @Schema(description = "Base URL of the observer server associated with this subscription", example = "https://observer.blindata.dev/api/v1")
    private String observerBaseUrl;

    @Schema(description = "API version of the observer server", example = "v1")
    private String observerApiVersion;

    @Schema(description = "List of event types this subscription is subscribed to.")
    private List<EventV2SubscriptionEventTypeResource> eventTypes;

    public EventV2SubscriptionResource() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getObserverBaseUrl() {
        return observerBaseUrl;
    }

    public void setObserverBaseUrl(String observerBaseUrl) {
        this.observerBaseUrl = observerBaseUrl;
    }

    public String getObserverApiVersion() {
        return observerApiVersion;
    }

    public void setObserverApiVersion(String observerApiVersion) {
        this.observerApiVersion = observerApiVersion;
    }

    public List<EventV2SubscriptionEventTypeResource> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<EventV2SubscriptionEventTypeResource> eventTypes) {
        this.eventTypes = eventTypes;
    }
}
