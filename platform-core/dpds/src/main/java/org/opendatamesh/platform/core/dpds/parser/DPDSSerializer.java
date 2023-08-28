package org.opendatamesh.platform.core.dpds.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class DPDSSerializer {

    public DPDSSerializer() {

    }

    public String serialize(
            DataProductVersionDPDS dataProductVersionRes,
            String form,
            String mediaType,
            boolean prettyPrint) throws JsonProcessingException {
        String result = null;

        ObjectMapper mapper = null;

        if (mediaType == null)
            mediaType = "json";
        if (form == null)
            form = "canonical";

        if (mediaType.equalsIgnoreCase("yaml")) {
            mapper = ObjectMapperFactory.YAML_MAPPER;
        } else {
            mapper = ObjectMapperFactory.JSON_MAPPER;
        }

        if (form.equalsIgnoreCase("normalized")) {
            result = serializeToNormalForm(dataProductVersionRes, mapper, prettyPrint);
        } else {
            result = serializeToCanonicalForm(dataProductVersionRes, false, mapper, prettyPrint);
        }

        return result;
    }

    private String serializeToNormalForm(
            DataProductVersionDPDS dataProductVersionRes,
            ObjectMapper mapper,
            boolean prettyPrint) throws JsonProcessingException {
        String result = null;

        if (prettyPrint) {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataProductVersionRes);
        } else {
            result = mapper.writeValueAsString(dataProductVersionRes);
        }

        return result;
    }

    public String serializeToCanonicalForm(
            DataProductVersionDPDS dataProductVersion,
            boolean rootOnly,
            ObjectMapper mapper,
            boolean prettyPrint) throws JsonProcessingException {

        String result = null;
        ObjectNode resultRootNode = null;

        String rootRawContent = dataProductVersion.getRawContent();

        ObjectNode rootNode = (ObjectNode) ObjectMapperFactory.getRightMapper(rootRawContent).readTree(rootRawContent);

        if (rootOnly == true) {
            resultRootNode = rootNode;
        } else {
            resultRootNode = mapper.createObjectNode();
            resultRootNode.set("dataProductDescriptor", rootNode.get("dataProductDescriptor"));
            resultRootNode.set("info", rootNode.get("info"));

            if (dataProductVersion.getInterfaceComponents() != null) {
                resultRootNode.set("interfaceComponents", getRawContent(dataProductVersion.getInterfaceComponents()));
            }
            if (dataProductVersion.getInternalComponents() != null) {
                resultRootNode.set("internalComponents", getRawContent(dataProductVersion.getInternalComponents()));
            }

        }

        if (prettyPrint) {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultRootNode);
        } else {
            result = mapper.writeValueAsString(resultRootNode);
        }

        return result;
    }

    private JsonNode getRawContent(InterfaceComponentsDPDS resources) throws JsonProcessingException {
        return getRawContent(resources, new HashSet<EntityTypeDPDS>(Arrays.asList(EntityTypeDPDS.values())));
    }

    private JsonNode getRawContent(InterfaceComponentsDPDS resources, Set<EntityTypeDPDS> inludedPortTypes)
            throws JsonProcessingException {

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode interfaceComponentsNode = mapper.createObjectNode();

        ArrayNode portsNode = null;
        if (inludedPortTypes.contains(EntityTypeDPDS.INPUTPORT)) {
            portsNode = getRawContent(resources.getInputPorts());
            if (portsNode.size() > 0)
                interfaceComponentsNode.set(EntityTypeDPDS.INPUTPORT.groupingPropertyName(), portsNode);
        }

        if (inludedPortTypes.contains(EntityTypeDPDS.OUTPUTPORT)) {
            portsNode = getRawContent(resources.getOutputPorts());
            // include event if it is empty to make the validator happy :)
            interfaceComponentsNode.set(EntityTypeDPDS.OUTPUTPORT.groupingPropertyName(), portsNode);
        }

        if (inludedPortTypes.contains(EntityTypeDPDS.CONTROLPORT)) {
            portsNode = getRawContent(resources.getControlPorts());
            if (portsNode.size() > 0)
                interfaceComponentsNode.set(EntityTypeDPDS.CONTROLPORT.groupingPropertyName(), portsNode);
        }

        if (inludedPortTypes.contains(EntityTypeDPDS.DISCOVERYPORT)) {
            portsNode = getRawContent(resources.getDiscoveryPorts());
            if (portsNode.size() > 0)
                interfaceComponentsNode.set(EntityTypeDPDS.DISCOVERYPORT.groupingPropertyName(), portsNode);
        }

        if (inludedPortTypes.contains(EntityTypeDPDS.OBSERVABILITYPORT)) {
            portsNode = getRawContent(resources.getObservabilityPorts());
            if (portsNode.size() > 0)
                interfaceComponentsNode.set(EntityTypeDPDS.OBSERVABILITYPORT.groupingPropertyName(), portsNode);
        }

        return interfaceComponentsNode;
    }

    public JsonNode getRawContent(InternalComponentsDPDS resources) throws JsonProcessingException {

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode internalComponentsNode = mapper.createObjectNode();

        internalComponentsNode.set("lifecycleInfo", resources.getActivityRawContent());
        internalComponentsNode.set("applicationComponents", getRawContent(resources.getApplicationComponents()));
        internalComponentsNode.set("infrastructuralComponents",
                getRawContent(resources.getInfrastructuralComponents()));

        return internalComponentsNode;
    }

    private ArrayNode getRawContent(List<? extends ComponentDPDS> components) throws JsonProcessingException {

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

        ArrayNode interfaceComponentsNode = mapper.createArrayNode();

        for (ComponentDPDS component : components) {
            String componentRawContent = component.getRawContent();
            JsonNode componentNode = ObjectMapperFactory.getRightMapper(componentRawContent)
                    .readTree(componentRawContent);
            interfaceComponentsNode.add(componentNode);
        }

        return interfaceComponentsNode;
    }
}