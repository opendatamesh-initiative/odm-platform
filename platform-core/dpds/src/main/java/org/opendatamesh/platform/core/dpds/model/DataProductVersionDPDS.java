package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductVersionDPDS implements Cloneable {

    @JsonProperty("dataProductDescriptor")
    private String dataProductDescriptor;

    @JsonProperty("info")
    private InfoDPDS info;

    @JsonProperty("interfaceComponents")
    private InterfaceComponentsDPDS interfaceComponents;

    @JsonProperty("internalComponents")
    private InternalComponentsDPDS internalComponents;

    @JsonProperty("components")
    private ComponentsDPDS components;

    @JsonProperty("tags")
    protected List<String> tags = new ArrayList<String>();

    @JsonProperty("externalDocs")
    private ExternalResourceDPDS externalDocs;

    @JsonIgnore
    private String rawContent;

    @JsonAnySetter
    public void ignored(String name, Object value) {
        //System.out.println(name + " : " + value + " : " + value.getClass().getName());
    }
    
    @JsonIgnore
    public void setRawContent(String content) {
        
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;
        try {
            ObjectNode rootNode = (ObjectNode)objectMapper.readTree(content);
            ObjectNode interfaceComponentsNode = (ObjectNode)rootNode.get("interfaceComponents");
            if(interfaceComponentsNode != null) {
                interfaceComponents.setRawContent(interfaceComponentsNode);
                rootNode.remove("interfaceComponents");
            }
            
            ObjectNode internalComponentsNode = (ObjectNode)rootNode.get("internalComponents");
            if(internalComponentsNode != null) {
                internalComponents.setRawContent(internalComponentsNode);
                rootNode.remove("internalComponents");
            }
            rawContent = objectMapper.writeValueAsString(rootNode);
        } catch  (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO this is orrible. Fix asap
    public String toEventString() throws JsonProcessingException {
        return ObjectMapperFactory.JSON_MAPPER.writeValueAsString(this).replace("versionNumber", "version");
    }

    public boolean hasInternalComponents() {
        return internalComponents != null;    
    }

    public boolean hasLifecycleInfo() {
        return hasInternalComponents() && internalComponents.hasLifecycleInfo();
    }


}
