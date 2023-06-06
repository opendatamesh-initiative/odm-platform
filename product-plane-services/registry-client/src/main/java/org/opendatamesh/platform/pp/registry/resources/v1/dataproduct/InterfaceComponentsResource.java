package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "inputPorts", "outputPorts",  "discoveryPorts", "observabilityPorts", "controlPorts"})
public class InterfaceComponentsResource extends ComponentContainerResource{

    @JsonProperty("inputPorts")
    private List<PortResource> inputPorts = new ArrayList<PortResource>();

    @JsonProperty("outputPorts")
    private List<PortResource> outputPorts = new ArrayList<PortResource>();

    @JsonProperty("discoveryPorts")
    private List<PortResource> discoveryPorts = new ArrayList<PortResource>();

    @JsonProperty("observabilityPorts")
    private List<PortResource> observabilityPorts = new ArrayList<PortResource>();

    @JsonProperty("controlPorts")
    private List<PortResource> controlPorts = new ArrayList<PortResource>();

    public List<PortResource> getPortListByEntityType(EntityType entityType) {
        switch (entityType) {
            case outputport:
                return outputPorts;
            case inputport:
                return inputPorts;
            case controlport:
                return controlPorts;
            case discoveryport:
                return discoveryPorts;
            case observabilityport:
                return observabilityPorts;
            default:
                return null;
        }
    }

    @JsonIgnore
    public void setRawContent(ObjectNode interfaceComponentsNode) throws JsonProcessingException {
        ArrayNode componentNodes = null;
        
        componentNodes = (ArrayNode)interfaceComponentsNode.get("inputPorts");
        if (componentNodes != null) {
            setRawContent(inputPorts, componentNodes);
        }

        componentNodes = (ArrayNode)interfaceComponentsNode.get("outputPorts");
        if (componentNodes != null) {
            setRawContent(outputPorts, componentNodes);
        }
      

        componentNodes = (ArrayNode)interfaceComponentsNode.get("controlPorts");
        if (componentNodes != null) {
            setRawContent(controlPorts, componentNodes);
        }

        componentNodes = (ArrayNode)interfaceComponentsNode.get("discoveryPorts");
        if (componentNodes != null) {
            setRawContent(discoveryPorts, componentNodes);
        }

        componentNodes = (ArrayNode)interfaceComponentsNode.get("observabilityPorts");
        if (componentNodes != null) {
            setRawContent(observabilityPorts, componentNodes);
        }
    }

    @JsonIgnore
    public  ObjectNode getRawContent() throws JsonProcessingException {
        return getRawContent( new HashSet<EntityType>(Arrays.asList( EntityType.values())) );
    }

    @JsonIgnore
    public  ObjectNode getRawContent(EntityType inludedPortType) throws JsonProcessingException {
        return getRawContent( new HashSet<EntityType>(Arrays.asList( new EntityType[] {inludedPortType} )) );
    }

    @JsonIgnore
    public  ObjectNode getRawContent(Set<EntityType> inludedPortTypes) throws JsonProcessingException {
       
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode interfaceComponentNodes = mapper.createObjectNode();
       
        if(inludedPortTypes.contains(EntityType.inputport))
            interfaceComponentNodes.set("inputPorts", getRawContent(inputPorts));
    
        if(inludedPortTypes.contains(EntityType.outputport))
            interfaceComponentNodes.set("outputPorts", getRawContent(outputPorts));

        if(inludedPortTypes.contains(EntityType.controlport))
            interfaceComponentNodes.set("controlPorts", getRawContent(controlPorts));

        if(inludedPortTypes.contains(EntityType.discoveryport))
            interfaceComponentNodes.set("discoveryPorts", getRawContent(discoveryPorts));

        if(inludedPortTypes.contains(EntityType.observabilityport))
            interfaceComponentNodes.set("observabilityPorts", getRawContent(observabilityPorts));

        return interfaceComponentNodes;
    }
}
