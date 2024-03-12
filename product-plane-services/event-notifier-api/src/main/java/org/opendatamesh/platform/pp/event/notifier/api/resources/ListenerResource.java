package org.opendatamesh.platform.pp.event.notifier.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListenerResource {

    @JsonProperty("id")
    @Schema(description = "Auto-generated ID of the Policy")
    private Long id;

    @JsonProperty("name")
    @Schema(description = "Unique name of the listening Notification Adapter")
    private String name;

    @JsonProperty("displayName")
    @Schema(description = "Human readable display name of the listening Notification Adapter")
    private String displayName;

    @JsonProperty("listenerUrl")
    @Schema(description = "URL to reach the listening Notification Adapter")
    private String listenerUrl;

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

    public String getListenerUrl() {
        return listenerUrl;
    }

    public void setListenerUrl(String listenerUrl) {
        this.listenerUrl = listenerUrl;
    }

}
