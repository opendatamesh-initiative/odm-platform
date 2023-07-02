package org.opendatamesh.platform.pp.registry.api.v1.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentTemplateResource {
    @JsonIgnore
    private ComponentTemplateId id;

    @JsonProperty("componentId")
    public String getComponentId() {
        return (id == null ? null: id.getComponentId());
    }

    @JsonProperty("templateId")
    public Long getTemplateId() {
        return (id == null ? null: id.getTemplateId());
    }

    @JsonProperty("componentType")
    public String getComponentType() {
        return (id == null ? null: id.getComponentType());
    }

    @JsonProperty("infoType")
    public String getInfoType() {
        return (id == null ? null: id.getInfoType());
    }

    @Data
    public static class ComponentTemplateId implements Serializable {

        @JsonProperty("componentId")
        String componentId;

        @JsonProperty("templateId")
        Long templateId;

        @JsonProperty("componentType")
        String componentType;

        @JsonProperty("infoType")
        String infoType;

    }
}
