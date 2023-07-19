package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "inputPorts", "outputPorts",  "discoveryPorts", "observabilityPorts", "controlPorts"})
public class InterfaceComponentsDPDS extends ComponentContainerDPDS{

    @JsonProperty("inputPorts")
    private List<PortDPDS> inputPorts = new ArrayList<PortDPDS>();

    @JsonProperty("outputPorts")
    private List<PortDPDS> outputPorts = new ArrayList<PortDPDS>();

    @JsonProperty("discoveryPorts")
    private List<PortDPDS> discoveryPorts = new ArrayList<PortDPDS>();

    @JsonProperty("observabilityPorts")
    private List<PortDPDS> observabilityPorts = new ArrayList<PortDPDS>();

    @JsonProperty("controlPorts")
    private List<PortDPDS> controlPorts = new ArrayList<PortDPDS>();

    public List<PortDPDS> getPortListByEntityType(EntityTypeDPDS entityType) {
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
        return getRawContent( new HashSet<EntityTypeDPDS>(Arrays.asList( EntityTypeDPDS.values())) );
    }

    @JsonIgnore
    public  ObjectNode getRawContent(EntityTypeDPDS inludedPortType) throws JsonProcessingException {
        return getRawContent( new HashSet<EntityTypeDPDS>(Arrays.asList( new EntityTypeDPDS[] {inludedPortType} )) );
    }

    @JsonIgnore
    public  ObjectNode getRawContent(Set<EntityTypeDPDS> inludedPortTypes) throws JsonProcessingException {
       
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode interfaceComponentNodes = mapper.createObjectNode();
       
        if(inludedPortTypes.contains(EntityTypeDPDS.inputport))
            interfaceComponentNodes.set("inputPorts", getRawContent(inputPorts));
    
        if(inludedPortTypes.contains(EntityTypeDPDS.outputport))
            interfaceComponentNodes.set("outputPorts", getRawContent(outputPorts));

        if(inludedPortTypes.contains(EntityTypeDPDS.controlport))
            interfaceComponentNodes.set("controlPorts", getRawContent(controlPorts));

        if(inludedPortTypes.contains(EntityTypeDPDS.discoveryport))
            interfaceComponentNodes.set("discoveryPorts", getRawContent(discoveryPorts));

        if(inludedPortTypes.contains(EntityTypeDPDS.observabilityport))
            interfaceComponentNodes.set("observabilityPorts", getRawContent(observabilityPorts));

        return interfaceComponentNodes;
    }
}
