package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyEngineResource extends TimestampedResource {
    private Long id;
    private String name;
    private String displayName;
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
