package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalComponentsResource extends ComponentContainerResource{

    @JsonProperty("applicationComponents")
    private List<ApplicationComponentResource> applicationComponents = new ArrayList<ApplicationComponentResource>();

    @JsonProperty("infrastructuralComponents")
    private List<InfrastructuralComponentResource> infrastructuralComponents = new ArrayList<InfrastructuralComponentResource>();

    public void replaceInfrastructuralComponent(InfrastructuralComponentResource oldDefinition,
            InfrastructuralComponentResource newDefinition) {
        this.infrastructuralComponents.remove(oldDefinition);
        this.infrastructuralComponents.add(newDefinition);
    }

    public void replaceApplicationComponent(ApplicationComponentResource oldDefinition,
            ApplicationComponentResource newDefinition) {
        this.applicationComponents.remove(oldDefinition);
        this.applicationComponents.add(newDefinition);
    }

    @JsonIgnore
    public void setRawContent(HashMap<String, List> map) throws JsonProcessingException {
        if (map.containsKey("applicationComponents")) {
            setRawContent(applicationComponents, map.get("applicationComponents"));
        }

        if (map.containsKey("infrastructuralComponents")) {
            setRawContent(infrastructuralComponents, map.get("infrastructuralComponents"));
        }
    }

    @JsonIgnore
    public HashMap<String, List> getRawContent() throws JsonProcessingException {
        HashMap<String, List> internalComponentsRawProperties = new HashMap<String, List>();
        internalComponentsRawProperties.put("applicationComponents", getRawContent(applicationComponents));
        internalComponentsRawProperties.put("infrastructuralComponents", getRawContent(infrastructuralComponents));
        return internalComponentsRawProperties;
    }

   
}
