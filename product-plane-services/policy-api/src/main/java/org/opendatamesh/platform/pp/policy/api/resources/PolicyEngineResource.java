package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyEngineResource extends TimestampedResource {

    @JsonProperty("id")
    @Schema(description = "Auto-generated ID of the PolicyEngine")
    private Long id;

    @JsonProperty("name")
    @Schema(description = "Unique name of the PolicyEngine")
    private String name;

    @JsonProperty("displayName")
    @Schema(description = "Human readable display name of the PolicyEngine")
    private String displayName;

    @JsonProperty("adapterUrl")
    @Schema(description = "URL to reach the PolicyEngine")
    private String adapterUrl;

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

    public String getAdapterUrl() {
        return adapterUrl;
    }

    public void setAdapterUrl(String adapterUrl) {
        this.adapterUrl = adapterUrl;
    }

}
