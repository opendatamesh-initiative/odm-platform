package org.opendatamesh.platform.pp.registry.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.BuildInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DeployInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.EntityType;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InterfaceComponentsResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InternalComponentsResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ProvisionInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.BuildInfoResourceDeserializer;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DeployInfoResourceDeserializer;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.ProvisionInfoResourceDeserializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DataProductVersionMapper extends ObjectMapper {
    
    
    private static DataProductVersionMapper mapper;

    public static DataProductVersionMapper getMapper() {
        if(mapper == null) mapper = new DataProductVersionMapper();
        return mapper;
    }


    private  DataProductVersionMapper() {
        
        setSerializationInclusion(Include.NON_EMPTY);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ProvisionInfoResource.class, new ProvisionInfoResourceDeserializer());
        module.addDeserializer(BuildInfoResource.class, new BuildInfoResourceDeserializer());
        module.addDeserializer(DeployInfoResource.class, new DeployInfoResourceDeserializer());

        registerModule(module);
    }

    public String getParsedContentAsString(DataProductVersionResource dataProductVersionRes, boolean prettyPrint) throws ParseException {
        String result = null;
        try {
            if(prettyPrint) {
                result = DataProductVersionMapper.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dataProductVersionRes);
            } else {
                result = DataProductVersionMapper.getMapper().writeValueAsString(dataProductVersionRes);
            }
            
        } catch (JsonProcessingException e) {
            throw new ParseException("Impossible to serialize as json string the parsed content", e);
        }
        return result;
    }

    public String getRawContent(DataProductVersionResource dataProductVersion, boolean rootOnly)  {
        String content = null;

        String rootRawContent = dataProductVersion.getRawContent(rootOnly);

        if(rootOnly == true) {
            return rootRawContent;
        } 

        try {
            ObjectNode rootNode = (ObjectNode)readTree(rootRawContent);

            ObjectNode resultRootNode = createObjectNode();
            resultRootNode.set("dataProductDescriptor", rootNode.get("dataProductDescriptor"));
            resultRootNode.set("info", rootNode.get("info"));
           
            resultRootNode.set("interfaceComponents", getRawContent(dataProductVersion.getInterfaceComponents()));
            resultRootNode.set("internalComponents", getRawContent(dataProductVersion.getInternalComponents()));

            content = writeValueAsString(resultRootNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return content;
    }

    public  JsonNode getRawContent(InterfaceComponentsResource resources) throws JsonProcessingException {
        return getRawContent( resources, new HashSet<EntityType>(Arrays.asList( EntityType.values())) );
    }

    public  JsonNode getRawContent(InterfaceComponentsResource resources, EntityType inludedPortType) throws JsonProcessingException {
        return getRawContent( resources, new HashSet<EntityType>(Arrays.asList( new EntityType[] {inludedPortType} )) );
    }

    public  JsonNode getRawContent(InterfaceComponentsResource resources, Set<EntityType> inludedPortTypes) throws JsonProcessingException {
        
        ObjectNode interfaceComponentsNode = createObjectNode();

        if(inludedPortTypes.contains(EntityType.inputport))
            interfaceComponentsNode.set("inputPorts", getRawContent(resources.getInputPorts()));
        
        if(inludedPortTypes.contains(EntityType.outputport))
            interfaceComponentsNode.set("outputPorts", getRawContent(resources.getOutputPorts()));

        if(inludedPortTypes.contains(EntityType.controlport))
            interfaceComponentsNode.set("controlPorts", getRawContent(resources.getControlPorts()));

        if(inludedPortTypes.contains(EntityType.discoveryport))
            interfaceComponentsNode.set("discoveryPorts", getRawContent(resources.getDiscoveryPorts()));

        if(inludedPortTypes.contains(EntityType.observabilityport))
            interfaceComponentsNode.set("observabilityPorts", getRawContent(resources.getObservabilityPorts()));

       return interfaceComponentsNode;
    }

    public  JsonNode getRawContent(InternalComponentsResource resources) throws JsonProcessingException {
        
        ObjectNode internalComponentsNode = createObjectNode();

        internalComponentsNode.set("applicationComponents", getRawContent(resources.getApplicationComponents()));
        internalComponentsNode.set("infrastructuralComponents", getRawContent(resources.getInfrastructuralComponents()));
        
        return internalComponentsNode;
    }

    public ArrayNode getRawContent(List<? extends ComponentResource> components) throws JsonProcessingException  {
        
        ArrayNode interfaceComponentsNode = createArrayNode();

        for (ComponentResource component : components) {
            String componentRawContent = component.getRawContent();
            JsonNode componentNode = readTree(componentRawContent);
            interfaceComponentsNode.add(componentNode);
        }

        return interfaceComponentsNode;
    }
}
