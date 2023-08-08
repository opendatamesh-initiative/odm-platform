package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalComponentsDPDS extends ComponentContainerDPDS{

    @JsonProperty("applicationComponents")
    private List<ApplicationComponentDPDS> applicationComponents = new ArrayList<ApplicationComponentDPDS>();

    @JsonProperty("infrastructuralComponents")
    private List<InfrastructuralComponentDPDS> infrastructuralComponents = new ArrayList<InfrastructuralComponentDPDS>();

    @JsonProperty("lifecycleInfo") 
    private LifecycleInfoDPDS lifecycleInfo;

    public void setRawContent(ObjectNode internalComponentsNode) throws JsonProcessingException {
       
        ObjectNode lifecycleNode = (ObjectNode)internalComponentsNode.get("lifecycleInfo");
        if (lifecycleNode != null) {
            setActivityRawContent(lifecycleNode);
        }

        ArrayNode applicationComponentNodes = (ArrayNode)internalComponentsNode.get("applicationComponents");
        if (applicationComponentNodes != null) {
            setRawContent(applicationComponents, applicationComponentNodes);
        }

        ArrayNode infrastructuralComponentNodes = (ArrayNode)internalComponentsNode.get("infrastructuralComponents");
        if (infrastructuralComponentNodes != null) {
            setRawContent(infrastructuralComponents, infrastructuralComponentNodes);
        }
    }

    @JsonIgnore
    public void setActivityRawContent(ObjectNode lifecycleInfoNode) throws JsonProcessingException {
        Iterator<String> stageIterator = lifecycleInfoNode.fieldNames();
        while(stageIterator.hasNext()) {
            String stageName = stageIterator.next();
            ObjectNode activityNode = (ObjectNode)lifecycleInfoNode.get(stageName);
            activityNode.put("stageName", stageName);
            lifecycleInfo.getActivityInfo(stageName).setRawContent(
                ObjectMapperFactory.JSON_MAPPER.writeValueAsString(activityNode));
        }
    }

    @JsonIgnore
    public ObjectNode getRawContent() throws JsonProcessingException {
        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode internalComponentsNode = mapper.createObjectNode();

        ObjectNode lifecycleNode = getActivityRawContent();
        internalComponentsNode.set("lifecycleInfo",lifecycleNode );
        internalComponentsNode.set("applicationComponents", getRawContent(applicationComponents));
        internalComponentsNode.set("infrastructuralComponents", getRawContent(infrastructuralComponents));
        return internalComponentsNode;
    }

    @JsonIgnore
    public ObjectNode getActivityRawContent() throws JsonProcessingException {

        if(lifecycleInfo == null) return null; // nothing to do
        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode lifecycleNode = mapper.createObjectNode();
        for(LifecycleActivityInfoDPDS activity: lifecycleInfo.getActivityInfos()) {
            ObjectNode activityNode = (ObjectNode)mapper.readTree(activity.getRawContent());
            String stageName = activityNode.get("stageName").asText();
            activityNode.remove("stageName");
            lifecycleNode.set(stageName, activityNode);
        }
        return lifecycleNode;
    }

    public boolean hasLifecycleInfo() {
        return lifecycleInfo != null;
    }
}
