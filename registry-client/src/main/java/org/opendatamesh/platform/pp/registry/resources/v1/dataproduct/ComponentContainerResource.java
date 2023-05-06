package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ComponentContainerResource {
    @JsonIgnore
    public void setRawContent(List<? extends ComponentResource> components, List<HashMap> componentsRawProperties) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        for (int i = 0; i < componentsRawProperties.size(); i++) {
            HashMap<String, List> componentRawProperties = componentsRawProperties.get(i);
            String rawContent = objectMapper.writeValueAsString(componentRawProperties);
            components.get(i).setRawContent(rawContent);
        }
    }

    @JsonIgnore
    public List<HashMap> getRawContent(List<? extends ComponentResource> components) throws JsonProcessingException  {
        ObjectMapper objectMapper = new ObjectMapper();

        List<HashMap> componentsRawProperties = new ArrayList();
        for (ComponentResource component : components) {
            String componentRawContent = component.getRawContent();
            HashMap<String, List> componetRawProperties = objectMapper.readValue(componentRawContent, HashMap.class);
            componentsRawProperties.add(componetRawProperties);
        }

        return componentsRawProperties;
    }

}
