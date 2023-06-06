package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    public void setRawContent(HashMap<String, List> map) throws JsonProcessingException {
        
        if (map.containsKey("inputPorts")) {
            setRawContent(inputPorts, map.get("inputPorts"));
        }

        if (map.containsKey("outputPorts")) {
            setRawContent(outputPorts, map.get("outputPorts"));
        }

        if (map.containsKey("controlPorts")) {
            setRawContent(controlPorts, map.get("controlPorts"));
        }

        if (map.containsKey("discoveryPorts")) {
            setRawContent(discoveryPorts, map.get("discoveryPorts"));
        }
        if (map.containsKey("observabilityPorts")) {
            setRawContent(observabilityPorts, map.get("observabilityPorts"));
        }
    }

    @JsonIgnore
    public  HashMap<String, List> getRawContent() throws JsonProcessingException {
        return getRawContent( new HashSet<EntityType>(Arrays.asList( EntityType.values())) );
    }

    @JsonIgnore
    public  HashMap<String, List> getRawContent(EntityType inludedPortType) throws JsonProcessingException {
        return getRawContent( new HashSet<EntityType>(Arrays.asList( new EntityType[] {inludedPortType} )) );
    }

    @JsonIgnore
    public  HashMap<String, List> getRawContent(Set<EntityType> inludedPortTypes) throws JsonProcessingException {
        HashMap<String, List> interfaceComponentsRawProperties = new HashMap<String, List>();

        if(inludedPortTypes.contains(EntityType.inputport))
            interfaceComponentsRawProperties.put("inputPorts", getRawContent(inputPorts));
        
        if(inludedPortTypes.contains(EntityType.outputport))
            interfaceComponentsRawProperties.put("outputPorts", getRawContent(outputPorts));

        if(inludedPortTypes.contains(EntityType.controlport))
            interfaceComponentsRawProperties.put("controlPorts", getRawContent(controlPorts));

        if(inludedPortTypes.contains(EntityType.discoveryport))
            interfaceComponentsRawProperties.put("discoveryPorts", getRawContent(discoveryPorts));

        if(inludedPortTypes.contains(EntityType.observabilityport))
            interfaceComponentsRawProperties.put("observabilityPorts", getRawContent(observabilityPorts));

       return interfaceComponentsRawProperties;
    }
}
