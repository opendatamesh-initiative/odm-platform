package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedPolicyResource {

    @JsonProperty("content")
    List<PolicyResource> content;

    public List<PolicyResource> getContent() {
        return content;
    }

}
