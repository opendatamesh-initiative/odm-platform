package org.opendatamesh.platform.pp.event.notifier.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.core.commons.resources.utils.TimestampedResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ObserverResource extends TimestampedResource {

    @JsonProperty("id")
    @Schema(description = "Auto-generated ID of the Policy")
    private Long id;

    @JsonProperty("name")
    @Schema(description = "Unique name of the listening Notification Adapter")
    private String name;

    @JsonProperty("displayName")
    @Schema(description = "Human readable display name of the listening Notification Adapter")
    private String displayName;

    @JsonProperty("observerServerBaseUrl")
    @Schema(description = "Observer server base URL (i.e., protocol, hostname and port) to reach the listening Notification Adapter")
    private String observerServerBaseUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getObserverServerBaseUrl() {
        return observerServerBaseUrl;
    }

    public void setObserverServerBaseUrl(String observerUrl) {
        this.observerServerBaseUrl = observerUrl;
    }

}
