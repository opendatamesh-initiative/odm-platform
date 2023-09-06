package org.opendatamesh.platform.core.dpds.parser;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.ComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.InternalComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DPDSDeserializer {

    public DataProductVersionDPDS deserialize(String descriptorContent) throws DeserializationException {

        DataProductVersionDPDS descriptorResource = null;

        ObjectMapper mapper = ObjectMapperFactory.getRightMapper(descriptorContent);
        try {
            descriptorResource = mapper.readValue(descriptorContent, DataProductVersionDPDS.class);
        } catch (JsonProcessingException e) {
            throw new DeserializationException("Descriptor document format is not valid", e);
        }
        setRootEntityRawContent(descriptorResource, descriptorContent, mapper);

        return descriptorResource;
    }

    public <T extends ComponentDPDS> T deserializeComponent(String componentContent, Class<T> componentType)
            throws DeserializationException {

        T componentResource = null;

        ObjectMapper mapper = ObjectMapperFactory.getRightMapper(componentContent);
        try {
            componentResource = mapper.readValue(componentContent, componentType);
            ObjectNode componentNode = (ObjectNode) mapper.readTree(componentContent);
            setComponetRawContent(componentResource, componentNode, mapper);
        } catch (Throwable t) {
            throw new DeserializationException("Impossible to deserialize component", t);
        }

        return componentResource;
    }

    public void setRootEntityRawContent(DataProductVersionDPDS descriptorResource, String descriptorContent,
            ObjectMapper mapper)
            throws DeserializationException {

        try {
            ObjectNode rootNode = (ObjectNode) mapper.readTree(descriptorContent);

            ObjectNode interfaceComponentsNode = (ObjectNode) rootNode.get("interfaceComponents");
            if (interfaceComponentsNode != null) {
                setInterfaceComponentsRawContent(descriptorResource.getInterfaceComponents(), interfaceComponentsNode,
                        mapper);
                rootNode.remove("interfaceComponents");
            }

            ObjectNode internalComponentsNode = (ObjectNode) rootNode.get("internalComponents");
            if (internalComponentsNode != null) {
                setInternalComponentsRawContent(descriptorResource.getInternalComponents(), internalComponentsNode,
                        mapper);
                rootNode.remove("internalComponents");
            }

            ObjectNode componentsNode = (ObjectNode) rootNode.get("components");
            if (componentsNode != null) {
                setSharedComponentsRawContent(descriptorResource.getComponents(), componentsNode, mapper);
                rootNode.remove("components");
            }

            descriptorResource.setRawContent(mapper.writeValueAsString(rootNode));
        } catch (Throwable t) {
            throw new DeserializationException("Impossible to deserialize descriptor", t);
        }
    }

    public void setInterfaceComponentsRawContent(
            InterfaceComponentsDPDS interfaceComponentsResource,
            ObjectNode interfaceComponentsNode,
            ObjectMapper mapper) throws JsonProcessingException {

        for (EntityTypeDPDS entityType : EntityTypeDPDS.PORTS) {
            ArrayNode componentNodes = (ArrayNode) interfaceComponentsNode.get(entityType.groupingPropertyName());
            if (componentNodes != null) {
                setComponetsRawContent(
                        interfaceComponentsResource.getPortListByEntityType(entityType),
                        componentNodes, mapper);
            }
        }
    }

    public void setInternalComponentsRawContent(
            InternalComponentsDPDS internalComponents,
            ObjectNode internalComponentsNode,
            ObjectMapper mapper) throws JsonProcessingException {

        ObjectNode lifecycleNode = (ObjectNode) internalComponentsNode.get("lifecycleInfo");
        if (lifecycleNode != null) {
            setActivityRawContent(internalComponents.getLifecycleInfo(), lifecycleNode, mapper);
        }

        ArrayNode applicationComponentNodes = (ArrayNode) internalComponentsNode.get("applicationComponents");
        if (applicationComponentNodes != null) {
            setComponetsRawContent(internalComponents.getApplicationComponents(), applicationComponentNodes, mapper);
        }

        ArrayNode infrastructuralComponentNodes = (ArrayNode) internalComponentsNode.get("infrastructuralComponents");
        if (infrastructuralComponentNodes != null) {
            setComponetsRawContent(internalComponents.getInfrastructuralComponents(), infrastructuralComponentNodes,
                    mapper);
        }
    }

    public void setActivityRawContent(
            LifecycleInfoDPDS lifecycleInfoResource,
            ObjectNode lifecycleInfoNode,
            ObjectMapper mapper) throws JsonProcessingException {

        Iterator<String> stageIterator = lifecycleInfoNode.fieldNames();
        while (stageIterator.hasNext()) {
            String stageName = stageIterator.next();
            LifecycleActivityInfoDPDS activityResource = lifecycleInfoResource.getActivityInfo(stageName);
            ObjectNode activityNode = (ObjectNode) lifecycleInfoNode.get(stageName);
            activityNode.put("stageName", stageName);

            if (activityResource.hasTemplate()) {
                JsonNode templateNode = activityNode.remove("template");
                activityResource.getTemplate().setRawContent(
                        mapper.writeValueAsString(templateNode));
            }

            activityResource.setRawContent(
                    mapper.writeValueAsString(activityNode));
        }
    }

    private void setSharedComponentsRawContent(
            ComponentsDPDS sharedComponentsResource,
            ObjectNode sharedComponentsNode,
            ObjectMapper mapper) throws JsonProcessingException {

        for (EntityTypeDPDS entityType : EntityTypeDPDS.COMPONENTS) {

            ObjectNode componentNodes = (ObjectNode) sharedComponentsNode.get(entityType.groupingPropertyName());
            if (componentNodes != null) {
                for (Entry<String, JsonNode> p : componentNodes.properties()) {
                    JsonNode componentNode = p.getValue();
                    ComponentDPDS componentResource = sharedComponentsResource.getComponentsByEntityType(entityType)
                            .get(p.getKey());

                    setComponetRawContent(componentResource, componentNode, mapper);
                }
            }
        }
    }

    public void setComponetsRawContent(
            List<? extends ComponentDPDS> componentResources,
            ArrayNode componentNodes,
            ObjectMapper mapper) throws JsonProcessingException {

        for (int i = 0; i < componentNodes.size(); i++) {
            ObjectNode componentNode = (ObjectNode) componentNodes.get(i);
            ComponentDPDS componentResource = componentResources.get(i);
            setComponetRawContent(componentResource, componentNode, mapper);
        }
    }

    public void setComponetRawContent(
            ComponentDPDS componentResource,
            JsonNode componentNode,
            ObjectMapper mapper) throws JsonProcessingException {

        if (componentResource instanceof PortDPDS) {
            PortDPDS port = (PortDPDS) componentResource;
            if (port.hasApi()) {
                JsonNode apiNode = componentNode.at("/promises/api");
                ObjectNode promisesNode = (ObjectNode) componentNode.get("promises");
                promisesNode.remove("api");
                setComponetRawContent(port.getPromises().getApi(), apiNode, mapper);
            }
        }

        String rawContent = mapper.writeValueAsString(componentNode);
        componentResource.setRawContent(rawContent);
    }
}
