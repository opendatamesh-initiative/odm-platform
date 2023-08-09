package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

public abstract class ComponentContainerDPDS {

    public void setRawContent(List<? extends ComponentDPDS> components, ArrayNode componentNodes) throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;
        
        for (int i = 0; i < componentNodes.size(); i++) {
            JsonNode componentNode = componentNodes.get(i);
            String rawContent = objectMapper.writeValueAsString(componentNode);
            components.get(i).setRawContent(rawContent);
        }
    }

    @JsonIgnore
    public ArrayNode getRawContent(List<? extends ComponentDPDS> components) throws JsonProcessingException  {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

        ArrayNode componentNodes = objectMapper.createArrayNode();
        for (ComponentDPDS component : components) {
            String componentRawContent = component.getRawContent();
            ObjectNode componetNode = (ObjectNode)objectMapper.readTree(componentRawContent);
            componentNodes.add(componetNode);
        }

        return componentNodes;
    }

}
