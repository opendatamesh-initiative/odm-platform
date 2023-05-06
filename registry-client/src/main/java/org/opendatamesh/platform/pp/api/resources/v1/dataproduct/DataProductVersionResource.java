package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductVersionResource implements Cloneable{

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

    public DataProductVersionResource() {
    }

    public DataProductVersionResource(InfoResource info) {
        this.info = info;
    }

    public DataProductVersionResource clone() throws CloneNotSupportedException
    {
        return (DataProductVersionResource) super.clone();
    }
    

    @JsonIgnore
    public void setRawContent(String content) {
        HashMap<String, HashMap> map;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            map = objectMapper.readValue(content, HashMap.class);
            if(map.get("interfaceComponents") != null) {
                interfaceComponents.setRawContent(map.get("interfaceComponents"));
                map.remove("interfaceComponents");
            }
            
            if(map.get("internalComponents") != null) {
                internalComponents.setRawContent(map.get("internalComponents"));
                map.remove("internalComponents");
            }
            rawContent = objectMapper.writeValueAsString(map);
        } catch  (Exception e) {
            e.printStackTrace();
        }
    }

    @JsonIgnore
    public String getRawContent()  {
       return getRawContent(true);
    }

    @JsonIgnore
    public String getRawContent(boolean rootOnly)  {
        String content = null;

        if(rootOnly == true) {
            return this.rawContent;
        } 

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LinkedHashMap descriptorProperties = new LinkedHashMap();
            HashMap rootEntityProperties = objectMapper.readValue(rawContent, HashMap.class);
            descriptorProperties.put("dataProductDescriptor", rootEntityProperties.get("dataProductDescriptor"));
            descriptorProperties.put("info", rootEntityProperties.get("info"));
            descriptorProperties.put("interfaceComponents", interfaceComponents.getRawContent());
            descriptorProperties.put("internalComponents", internalComponents.getRawContent());
            
            content = objectMapper.writeValueAsString(descriptorProperties);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return content;
    }

    @JsonIgnore
    public String getComponentRawContent(boolean rootOnly)  {
        String content = null;

        if(rootOnly == true) {
            return this.rawContent;
        } 

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LinkedHashMap descriptorProperties = new LinkedHashMap();
            HashMap rootEntityProperties = objectMapper.readValue(rawContent, HashMap.class);
            descriptorProperties.put("dataProductDescriptor", rootEntityProperties.get("dataProductDescriptor"));
            descriptorProperties.put("info", rootEntityProperties.get("info"));
            descriptorProperties.put("interfaceComponents", interfaceComponents.getRawContent());
            descriptorProperties.put("internalComponents", internalComponents.getRawContent());
            
            content = objectMapper.writeValueAsString(descriptorProperties);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return content;
    }
}
