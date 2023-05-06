package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalResourceResource {

    @JsonProperty("description")
    private String description;

    @JsonProperty("mediaType")
    private String mediaType;

    @JsonProperty("$href")
    private String href;

    public ExternalResourceResource() {
    }

    public ExternalResourceResource(String description, String mediaType, String href) {
        this.description = description;
        this.mediaType = mediaType;
        this.href = href;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public String toString() {
        return "ExternalDoc{" +
                "description='" + description + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", $href='" + href + '\'' +
                '}';
    }
}
