package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalComponentsDPDS extends ComponentContainerDPDS{

    @JsonProperty("applicationComponents")
    private List<ApplicationComponentDPDS> applicationComponents = new ArrayList<ApplicationComponentDPDS>();

    @JsonProperty("infrastructuralComponents")
    private List<InfrastructuralComponentDPDS> infrastructuralComponents = new ArrayList<InfrastructuralComponentDPDS>();


    public void setRawContent(ObjectNode internalComponentNodes) throws JsonProcessingException {
       

        ArrayNode applicationComponentNodes = (ArrayNode)internalComponentNodes.get("applicationComponents");
        if (applicationComponentNodes != null) {
            setRawContent(applicationComponents, applicationComponentNodes);
        }

        ArrayNode infrastructuralComponentNodes = (ArrayNode)internalComponentNodes.get("infrastructuralComponents");
        if (infrastructuralComponentNodes != null) {
            setRawContent(infrastructuralComponents, infrastructuralComponentNodes);
        }
    }

    @JsonIgnore
    public ObjectNode getRawContent() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode internalComponentsNode = mapper.createObjectNode();
        internalComponentsNode.set("applicationComponents", getRawContent(applicationComponents));
        internalComponentsNode.set("infrastructuralComponents", getRawContent(infrastructuralComponents));
        return internalComponentsNode;
    }

   
}
