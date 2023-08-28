package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentsDPDS {

    @JsonProperty("inputPorts")
    private Map<String, PortDPDS> inputPorts = new HashMap<>();

    @JsonProperty("outputPorts")
    private Map<String, PortDPDS> outputPorts = new HashMap<>();

    @JsonProperty("discoveryPorts")
    private Map<String, PortDPDS> discoveryPorts = new HashMap<>();

    @JsonProperty("observabilityPorts")
    private Map<String, PortDPDS> observabilityPorts = new HashMap<>();

    @JsonProperty("controlPorts")
    private Map<String, PortDPDS> controlPorts = new HashMap<>();

    @JsonProperty("applicationComponents")
    private Map<String, ApplicationComponentDPDS> applicationComponents = new HashMap<>();

    @JsonProperty("infrastructuralComponents")
    private Map<String, InfrastructuralComponentDPDS> infrastructuralComponents = new HashMap<>();

    @JsonProperty("templates")
    private Map<String, ObjectNode> templates = new HashMap<>();


    @SuppressWarnings("unchecked")
    public <E extends ComponentDPDS> Map<String, E> getComponentsByEntityType(EntityTypeDPDS type){
        switch (type){
            case INPUTPORT:
                return (Map<String, E>) inputPorts;
            case OUTPUTPORT:
                return (Map<String, E>) outputPorts;
            case DISCOVERYPORT:
                return (Map<String, E>) discoveryPorts;
            case CONTROLPORT:
                return (Map<String, E>) controlPorts;
            case OBSERVABILITYPORT:
                return (Map<String, E>) observabilityPorts;
            case APPLICATION:
                return (Map<String, E>) applicationComponents;
            case INFRASTRUCTURE:
                return (Map<String, E>) infrastructuralComponents;
            default:
                throw new RuntimeException("[" + type + "] is not a valid component type");
        } 
    }
}
