package org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources;

import java.util.List;

public class NotificationV2SubscribeRequestRes {
    private String name;
    private String displayName;
    private String observerBaseUrl;
    private String observerApiVersion;
    private List<String> eventTypes;

    public NotificationV2SubscribeRequestRes() {
    }

    public NotificationV2SubscribeRequestRes(String name, String displayName, String observerBaseUrl, String observerApiVersion, List<String> eventTypes) {
        this.name = name;
        this.displayName = displayName;
        this.observerBaseUrl = observerBaseUrl;
        this.observerApiVersion = observerApiVersion;
        this.eventTypes = eventTypes;
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

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }
}
