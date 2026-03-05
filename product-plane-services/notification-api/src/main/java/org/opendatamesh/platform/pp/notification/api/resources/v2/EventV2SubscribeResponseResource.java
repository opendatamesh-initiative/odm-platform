package org.opendatamesh.platform.pp.notification.api.resources.v2;

import java.util.List;

/**
 * Response and request type for V2 observer subscription (subscribe endpoint).
 */
public class EventV2SubscribeResponseResource {
    private EventV2SubscribeResource subscription;

    public EventV2SubscribeResponseResource() {
    }

    public EventV2SubscribeResource getSubscription() {
        return subscription;
    }

    public void setSubscription(EventV2SubscribeResource subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "EventV2SubscribeResponseResource{" +
                "subscription=" + (subscription != null ? subscription.toString() : "null") +
                '}';
    }

    /**
     * Request body for subscribe; also part of the response subscription.
     */
    public static class EventV2SubscribeResource {
        private String name;
        private String displayName;
        private String observerBaseUrl;
        private String observerApiVersion;
        private List<String> eventTypes;

        public EventV2SubscribeResource() {
        }

        public EventV2SubscribeResource(EventV2SubscribeResource other) {
            if (other == null) return;
            this.name = other.name;
            this.displayName = other.displayName;
            this.observerBaseUrl = other.observerBaseUrl;
            this.observerApiVersion = other.observerApiVersion;
            this.eventTypes = other.eventTypes;
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

        @Override
        public String toString() {
            return "EventV2SubscribeResource{" +
                    "name='" + name + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", observerBaseUrl='" + observerBaseUrl + '\'' +
                    ", observerApiVersion='" + observerApiVersion + '\'' +
                    ", eventTypes=" + eventTypes +
                    '}';
        }
    }
}
