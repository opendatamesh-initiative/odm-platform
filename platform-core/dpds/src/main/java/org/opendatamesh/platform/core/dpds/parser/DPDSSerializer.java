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

    ObjectMapper mapper;
    boolean prettyPrint;

    public static DPDSSerializer DEFAULT_JSON_SERIALIZER;
    public static DPDSSerializer DEFAULT_YAML_SERIALIZER;

    static {
        DEFAULT_JSON_SERIALIZER = new DPDSSerializer("json", true);
        DEFAULT_YAML_SERIALIZER = new DPDSSerializer("yaml", true);
    }

    public DPDSSerializer() {
        this("json", true);
    }

    public DPDSSerializer(String mediaType, boolean prettyPrint) {
        if (mediaType.equalsIgnoreCase("yaml")) {
            mapper = ObjectMapperFactory.YAML_MAPPER;
        } else {
            mapper = ObjectMapperFactory.JSON_MAPPER;
        }

        this.prettyPrint = prettyPrint;
    }

    public String serialize(
            DataProductVersionDPDS descriptorResource,
            String form) throws JsonProcessingException {

        String result = null;

        if (form.equalsIgnoreCase("canonical")) {
            result = serializeToCanonicalForm(descriptorResource);
        } else {
            result = writeValueAsString(descriptorResource);

        }

        return result;
    }

    public String serializeToCanonicalForm(
            DataProductVersionDPDS dataProductVersion) throws JsonProcessingException {

        String result = null;
        ObjectNode resultRootNode = null;

        String rootRawContent = dataProductVersion.getRawContent();

        ObjectNode rootNode = (ObjectNode) ObjectMapperFactory.getRightMapper(rootRawContent).readTree(rootRawContent);

        resultRootNode = mapper.createObjectNode();
        resultRootNode.set("dataProductDescriptor", rootNode.get("dataProductDescriptor"));
        resultRootNode.set("info", rootNode.get("info"));

        if (dataProductVersion.getInterfaceComponents() != null) {
            resultRootNode.set("interfaceComponents", getRawContent(dataProductVersion.getInterfaceComponents()));
        }
        if (dataProductVersion.getInternalComponents() != null) {
            resultRootNode.set("internalComponents", getRawContent(dataProductVersion.getInternalComponents()));
        }

        result = writeValueAsString(resultRootNode);

        return result;
    }

    private String writeValueAsString(Object value) throws JsonProcessingException {
        String result = null;

        if (prettyPrint) {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } else {
            result = mapper.writeValueAsString(value);
        }

        return result;
    }

    public String serialize(
            InterfaceComponentsDPDS interfaceComponents,
            EntityTypeDPDS entityType,
            String form) throws JsonProcessingException {

        String result = null;

        if (form.equalsIgnoreCase("canonical")) {
            result = serializeToCanonicalForm(interfaceComponents, entityType);
        } else {
            result = writeValueAsString(interfaceComponents);

        }

        return result;
    }

    public String serializeToCanonicalForm(
            InterfaceComponentsDPDS interfaceComponents,
            EntityTypeDPDS entityType) throws JsonProcessingException {

        JsonNode interfacesComponentsNode = null;
        if (entityType != null) {
            interfacesComponentsNode = getInterfaceComponentsRawContent(interfaceComponents,
                    new HashSet<EntityTypeDPDS>(Arrays.asList(entityType)));
        } else {
            interfacesComponentsNode = getRawContent(interfaceComponents);
        }

        return writeValueAsString(interfacesComponentsNode);
    }

    public String serializeToCanonicalForm(
            InternalComponentsDPDS internalComponents,
            EntityTypeDPDS entityType) throws JsonProcessingException {

        JsonNode internalComponentsNode = null;
        if (entityType != null) {
            internalComponentsNode = getRawContent(internalComponents,
                    new HashSet<EntityTypeDPDS>(Arrays.asList(entityType)));
        } else {
            internalComponentsNode = getRawContent(internalComponents);
        }

        return writeValueAsString(internalComponentsNode);
    }

    public <T extends ComponentDPDS> String serialize(
            List<T> components,
            String form
    ) throws JsonProcessingException {

        String result = null;

        if (form.equalsIgnoreCase("canonical")) {
            result = serializeToCanonicalForm(components);
        } else {
            result = writeValueAsString(components);

        }

        return result;
    }

    public <T extends ComponentDPDS> String serializeToCanonicalForm(
            List<T> components
    ) throws JsonProcessingException {

        String result = writeValueAsString(getComponetsRawContent(components));
        return result;
    }

    private JsonNode getRawContent(InterfaceComponentsDPDS resources) throws JsonProcessingException {
        return getInterfaceComponentsRawContent(resources,
                new HashSet<EntityTypeDPDS>(Arrays.asList(EntityTypeDPDS.values())));
    }

    private JsonNode getInterfaceComponentsRawContent(
            InterfaceComponentsDPDS interfaceComponentsResource,
            Set<EntityTypeDPDS> inludedInterfaceComponentTypes) throws JsonProcessingException {

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode interfaceComponentsNode = mapper.createObjectNode();

        ArrayNode portsNode = null;
        if (inludedInterfaceComponentTypes.contains(EntityTypeDPDS.INPUTPORT)) {
            portsNode = getComponetsRawContent(interfaceComponentsResource.getInputPorts());
            if (portsNode.size() > 0)
                interfaceComponentsNode.set(EntityTypeDPDS.INPUTPORT.groupingPropertyName(), portsNode);
        }

        if (inludedInterfaceComponentTypes.contains(EntityTypeDPDS.OUTPUTPORT)) {
            portsNode = getComponetsRawContent(interfaceComponentsResource.getOutputPorts());
            // include even if it is empty to make the validator happy :)
            interfaceComponentsNode.set(EntityTypeDPDS.OUTPUTPORT.groupingPropertyName(), portsNode);
        }

        if (inludedInterfaceComponentTypes.contains(EntityTypeDPDS.CONTROLPORT)) {
            portsNode = getComponetsRawContent(interfaceComponentsResource.getControlPorts());
            if (portsNode.size() > 0)
                interfaceComponentsNode.set(EntityTypeDPDS.CONTROLPORT.groupingPropertyName(), portsNode);
        }

        if (inludedInterfaceComponentTypes.contains(EntityTypeDPDS.DISCOVERYPORT)) {
            portsNode = getComponetsRawContent(interfaceComponentsResource.getDiscoveryPorts());
            if (portsNode.size() > 0)
                interfaceComponentsNode.set(EntityTypeDPDS.DISCOVERYPORT.groupingPropertyName(), portsNode);
        }

        if (inludedInterfaceComponentTypes.contains(EntityTypeDPDS.OBSERVABILITYPORT)) {
            portsNode = getComponetsRawContent(interfaceComponentsResource.getObservabilityPorts());
            if (portsNode.size() > 0)
                interfaceComponentsNode.set(EntityTypeDPDS.OBSERVABILITYPORT.groupingPropertyName(), portsNode);
        }

        return interfaceComponentsNode;
    }

    private JsonNode getRawContent(InternalComponentsDPDS resources) throws JsonProcessingException {
        return getRawContent(resources, new HashSet<EntityTypeDPDS>(Arrays.asList(EntityTypeDPDS.values())));
    }

    private JsonNode getRawContent(InternalComponentsDPDS resources, Set<EntityTypeDPDS> inludedInternalComponentTypes)
            throws JsonProcessingException {

        ObjectNode internalComponentsNode = mapper.createObjectNode();

        ArrayNode appNodes = null;
        if (inludedInternalComponentTypes.contains(EntityTypeDPDS.APPLICATION)) {
            appNodes = getComponetsRawContent(resources.getApplicationComponents());
            if (appNodes.size() > 0)
                internalComponentsNode.set(EntityTypeDPDS.APPLICATION.groupingPropertyName(), appNodes);
        }

        ArrayNode infraNodes = null;
        if (inludedInternalComponentTypes.contains(EntityTypeDPDS.INFRASTRUCTURE)) {
            infraNodes = getComponetsRawContent(resources.getInfrastructuralComponents());
            if (infraNodes.size() > 0)
                internalComponentsNode.set(EntityTypeDPDS.INFRASTRUCTURE.groupingPropertyName(), infraNodes);
        }

        internalComponentsNode.set("lifecycleInfo", getActivityRawContent(resources.getLifecycleInfo()));

        return internalComponentsNode;
    }

    private ObjectNode getActivityRawContent(LifecycleInfoDPDS lifecycleInfo) throws JsonProcessingException {

        if (lifecycleInfo == null)
            return null; // nothing to do
        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode lifecycleNode = mapper.createObjectNode();
        for (LifecycleActivityInfoDPDS activity : lifecycleInfo.getActivityInfos()) {
            ObjectNode activityNode = (ObjectNode) mapper.readTree(activity.getRawContent());
            String stageName = activityNode.get("stageName").asText();
            activityNode.remove("stageName");
            
            if(activity.hasTemplate()) {
                ObjectNode templateNode = (ObjectNode) mapper.readTree(activity.getTemplate().getRawContent());
                activityNode.set("template", templateNode);
            }

            lifecycleNode.set(stageName, activityNode);            
        }
        return lifecycleNode;
    }

    private ArrayNode getComponetsRawContent(List<? extends ComponentDPDS> components) throws JsonProcessingException {

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

        ArrayNode interfaceComponentsNode = mapper.createArrayNode();

        for (ComponentDPDS component : components) {
            JsonNode componentNode = getComponetRawContent(component);
            interfaceComponentsNode.add(componentNode);
        }

        return interfaceComponentsNode;
    }

    private ObjectNode getComponetRawContent(ComponentDPDS componentResource) throws JsonProcessingException {
        ObjectNode componentNode = null;

        String componentContent = componentResource.getRawContent();
        componentNode = (ObjectNode) ObjectMapperFactory.getRightMapper(componentContent)
                .readTree(componentContent);

        if (componentResource instanceof PortDPDS) {
            PortDPDS portResource = (PortDPDS) componentResource;
            if (portResource.hasApi()) {
                String apiContent = portResource.getPromises().getApi().getRawContent();
                JsonNode apiNode = ObjectMapperFactory.getRightMapper(apiContent).readTree(apiContent);
                ObjectNode promisesNode = (ObjectNode) componentNode.get("promises");
                promisesNode.set("api", apiNode);
            }
        }

        return componentNode;
    }

}
