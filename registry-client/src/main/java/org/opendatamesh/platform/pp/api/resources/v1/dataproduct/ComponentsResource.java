package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.*;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentsResource {

    @JsonProperty("inputPorts")
    private Map<String, PortResource> inputPorts = new HashMap<>();

    @JsonProperty("outputPorts")
    private Map<String, PortResource> outputPorts = new HashMap<>();

    @JsonProperty("discoveryPorts")
    private Map<String, PortResource> discoveryPorts = new HashMap<>();

    @JsonProperty("observabilityPorts")
    private Map<String, PortResource> observabilityPorts = new HashMap<>();

    @JsonProperty("controlPorts")
    private Map<String, PortResource> controlPorts = new HashMap<>();

    @JsonProperty("applicationComponents")
    private Map<String, ApplicationComponentResource> applicationComponents = new HashMap<>();

    @JsonProperty("infrastructuralComponents")
    private Map<String, InfrastructuralComponentResource> infrastructuralComponents = new HashMap<>();

    public void addInputPort(String key, PortResource value){
        this.inputPorts.put(key, value);
    }

    public void addOutputPort(String key, PortResource value){
        this.outputPorts.put(key, value);
    }

    public void addDiscoveryPort(String key, PortResource value){
        this.discoveryPorts.put(key, value);
    }

    
    public void addObservabilityPort(String key, PortResource value){
        this.observabilityPorts.put(key, value);
    }


    public void addControlPort(String key, PortResource value){
        this.controlPorts.put(key, value);
    }

    public <E extends ComponentResource> Map<String, E> getComponentsByEntityType(EntityType type){
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
