package org.opendatamesh.platform.core.serde;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opendatamesh.platform.core.ObjectMapperFactory;
import org.opendatamesh.platform.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.BuildInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DeployInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.EntityType;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InterfaceComponentsResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InternalComponentsResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ProvisionInfoResource;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

@Data
public class DataProductVersionSerializer {

   public DataProductVersionSerializer() {

   }
   

    public String serialize(
            DataProductVersionResource dataProductVersionRes,
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
            DataProductVersionResource dataProductVersionRes,
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
            DataProductVersionResource dataProductVersion,
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

            resultRootNode.set("interfaceComponents", getRawContent(dataProductVersion.getInterfaceComponents()));
            resultRootNode.set("internalComponents", getRawContent(dataProductVersion.getInternalComponents()));
        }

        if (prettyPrint) {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultRootNode);
        } else {
            result = mapper.writeValueAsString(resultRootNode);
        }

        return result;
    }

    private JsonNode getRawContent(InterfaceComponentsResource resources) throws JsonProcessingException {
        return getRawContent(resources, new HashSet<EntityType>(Arrays.asList(EntityType.values())));
    }

    private JsonNode getRawContent(InterfaceComponentsResource resources, EntityType inludedPortType)
            throws JsonProcessingException {
        return getRawContent(resources, new HashSet<EntityType>(Arrays.asList(new EntityType[] { inludedPortType })));
    }

    private JsonNode getRawContent(InterfaceComponentsResource resources, Set<EntityType> inludedPortTypes)
            throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode interfaceComponentsNode = mapper.createObjectNode();

        if (inludedPortTypes.contains(EntityType.inputport))
            interfaceComponentsNode.set("inputPorts", getRawContent(resources.getInputPorts()));

        if (inludedPortTypes.contains(EntityType.outputport))
            interfaceComponentsNode.set("outputPorts", getRawContent(resources.getOutputPorts()));

        if (inludedPortTypes.contains(EntityType.controlport))
            interfaceComponentsNode.set("controlPorts", getRawContent(resources.getControlPorts()));

        if (inludedPortTypes.contains(EntityType.discoveryport))
            interfaceComponentsNode.set("discoveryPorts", getRawContent(resources.getDiscoveryPorts()));

        if (inludedPortTypes.contains(EntityType.observabilityport))
            interfaceComponentsNode.set("observabilityPorts", getRawContent(resources.getObservabilityPorts()));

        return interfaceComponentsNode;
    }

    public JsonNode getRawContent(InternalComponentsResource resources) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode internalComponentsNode = mapper.createObjectNode();

        internalComponentsNode.set("applicationComponents", getRawContent(resources.getApplicationComponents()));
        internalComponentsNode.set("infrastructuralComponents",
                getRawContent(resources.getInfrastructuralComponents()));

        return internalComponentsNode;
    }

    private ArrayNode getRawContent(List<? extends ComponentResource> components) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        ArrayNode interfaceComponentsNode = mapper.createArrayNode();

        for (ComponentResource component : components) {
            String componentRawContent = component.getRawContent();
            JsonNode componentNode = ObjectMapperFactory.getRightMapper(componentRawContent).readTree(componentRawContent);
            interfaceComponentsNode.add(componentNode);
        }

        return interfaceComponentsNode;
    }
}
