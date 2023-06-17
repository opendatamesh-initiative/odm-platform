package org.opendatamesh.platform.core.resolvers;

import java.util.List;
import java.util.UUID;

import org.opendatamesh.platform.core.DataProductVersionSource;
import org.opendatamesh.platform.core.ObjectMapperFactory;
import org.opendatamesh.platform.core.serde.DataProductVersionSerializer;
import org.opendatamesh.platform.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.EntityType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReadOnlyPropertiesResolver implements PropertiesResolver {
    DataProductVersionResource dataProductVersionRes;
    DataProductVersionSource source;
    ObjectMapper mapper;

    public ReadOnlyPropertiesResolver(DataProductVersionResource dataProductVersionRes, DataProductVersionSource source) {
        this.dataProductVersionRes = dataProductVersionRes;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;

    }

    @Override
    public void resolve() throws ParseException {
       
        DataProductVersionResource parsedContent = dataProductVersionRes;

        addReadOnlyPropertiesToInfo();
        
        
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getInputPorts(), EntityType.inputport);
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getOutputPorts(), EntityType.outputport);
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getDiscoveryPorts(), EntityType.discoveryport);
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getObservabilityPorts(), EntityType.observabilityport);
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getControlPorts(), EntityType.controlport);

        addReadOnlyPropertiesToComponents(parsedContent.getInternalComponents().getApplicationComponents(), EntityType.application);
        addReadOnlyPropertiesToComponents(parsedContent.getInternalComponents().getInfrastructuralComponents(), EntityType.infrastructure);
        
    }

    private void addReadOnlyPropertiesToInfo() throws ParseException {
        String fqn, uuid;

        String rawContent = dataProductVersionRes.getRawContent();
        ObjectNode rootNode = null;
        try {
            rootNode = (ObjectNode)mapper.readTree(rawContent);
        } catch (Throwable t) {
            throw new ParseException("Impossible to parse descriptor raw cantent", t);
        }
        ObjectNode infoNode = (ObjectNode)rootNode.get("info");

        // Set field "entityType"
        dataProductVersionRes.getInfo().setEntityType(EntityType.dataproduct.toString()); 
        infoNode.put("entityType", EntityType.dataproduct.toString());

        // Set field "id"
        fqn = dataProductVersionRes.getInfo().getFullyQualifiedName();
        uuid = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
        dataProductVersionRes.getInfo().setDataProductId(uuid);
        infoNode.put("id", uuid);

        rootNode.set("info", infoNode);
        try {
            dataProductVersionRes.setRawContent(mapper.writeValueAsString(rootNode));
        } catch (Throwable t) {
            throw new ParseException("Impossible serialize descriptor", t);
        }
    }

    private void addReadOnlyPropertiesToComponents(List<? extends ComponentResource> components, EntityType entityType) throws ParseException  {
        String fqn, uuid;

        for(ComponentResource component : components) {
            ObjectNode componentNode;
            try {
                componentNode = (ObjectNode)mapper.readTree(component.getRawContent());
            } catch (Throwable t) {
                throw new ParseException("Impossible to parse component raw cantent", t);
            }
            
            // Set field "entityType"
            component.setEntityType(entityType);
            componentNode.put("entityType", entityType.toString());
            
            // Set field "id"
            fqn = (String)component.getFullyQualifiedName();
            uuid = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
            component.setId(uuid);
            componentNode.put("id", uuid);
            
            try {
                component.setRawContent(mapper.writeValueAsString(componentNode));
            } catch (Throwable t) {
                throw new ParseException("Impossible serialize component", t);
            }
            
        }
    }
    

    public static void resolve(DataProductVersionResource dataProductVersionRes, DataProductVersionSource source) throws ParseException {
        ReadOnlyPropertiesResolver resolver = new ReadOnlyPropertiesResolver(dataProductVersionRes, source);
        resolver.resolve();
    }
}
