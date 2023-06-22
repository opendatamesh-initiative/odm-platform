package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.*;


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

    public void addInputPort(String key, PortDPDS value){
        this.inputPorts.put(key, value);
    }

    public void addOutputPort(String key, PortDPDS value){
        this.outputPorts.put(key, value);
    }

    public void addDiscoveryPort(String key, PortDPDS value){
        this.discoveryPorts.put(key, value);
    }

    
    public void addObservabilityPort(String key, PortDPDS value){
        this.observabilityPorts.put(key, value);
    }


    public void addControlPort(String key, PortDPDS value){
        this.controlPorts.put(key, value);
    }

    public <E extends ComponentDPDS> Map<String, E> getComponentsByEntityType(EntityTypeDPDS type){
        switch (type){
            case inputport:
                return (Map<String, E>) inputPorts;
            case outputport:
                return (Map<String, E>) outputPorts;
            case discoveryport:
                return (Map<String, E>) discoveryPorts;
            case controlport:
                return (Map<String, E>) controlPorts;
            case observabilityport:
                return (Map<String, E>) observabilityPorts;
            case application:
                return (Map<String, E>) applicationComponents;
            case infrastructure:
                return (Map<String, E>) infrastructuralComponents;
        }
        return null;
    }
}
