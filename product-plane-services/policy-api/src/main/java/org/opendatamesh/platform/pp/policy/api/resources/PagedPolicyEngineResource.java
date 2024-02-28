package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedPolicyEngineResource {

    @JsonProperty("content")
    List<PolicyEngineResource> content;

    public List<PolicyEngineResource> getContent() {
        return content;
    }

}
