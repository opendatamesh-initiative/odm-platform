package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductVersionResource implements Cloneable {

    @JsonProperty("dataProductDescriptor")
    private String dataProductDescriptor;

    @JsonProperty("info")
    private InfoResource info;

    @JsonProperty("interfaceComponents")
    private InterfaceComponentsResource interfaceComponents;

    @JsonProperty("internalComponents")
    private InternalComponentsResource internalComponents;

    @JsonProperty("components")
    private ComponentsResource components;

    @JsonProperty("tags")
    protected List<String> tags = new ArrayList<String>();

    @JsonProperty("externalDocs")
    private ExternalResourceResource externalDocs;

    @JsonIgnore
    private String rawContent;

    @JsonAnySetter
    public void ignored(String name, Object value) {
        System.out.println(name + " : " + value + " : " + value.getClass().getName());
    }

    public DataProductVersionResource clone() throws CloneNotSupportedException
    {
        return (DataProductVersionResource) super.clone();
    }
    

    @JsonIgnore
    public void setRawContent(String content) {
        
        ObjectMapper objectMapper = new ObjectMapper();
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

    public String toEventString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this).replace("versionNumber", "version");
    }

}
