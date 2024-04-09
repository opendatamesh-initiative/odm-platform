package org.opendatamesh.platform.pp.event.notifier.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedObserverResource {

    @JsonProperty("content")
    List<ObserverResource> content;

    public List<ObserverResource> getContent() {
        return content;
    }

}
