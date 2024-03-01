package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedPolicyEvaluationResultResource {

    @JsonProperty("content")
    List<PolicyEvaluationResultResource> content;

    public List<PolicyEvaluationResultResource> getContent() {
        return content;
    }

}
