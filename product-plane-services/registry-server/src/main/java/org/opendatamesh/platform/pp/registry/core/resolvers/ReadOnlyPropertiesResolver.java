package org.opendatamesh.platform.pp.registry.core.resolvers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.opendatamesh.platform.pp.registry.core.DataProductDescriptor;
import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.core.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.EntityType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ReadOnlyPropertiesResolver implements PropertiesResolver {
    DataProductDescriptor descriptor;

    public ReadOnlyPropertiesResolver(DataProductDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void resolve() throws ParseException {
       
        DataProductVersionResource parsedContent = descriptor.getParsedContent();

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

        DataProductVersionResource parsedContent = descriptor.getParsedContent();
        String rawContent = descriptor.getParsedContent().getRawContent();

        
        Map<String, Map> rootEntityProperties;
        try {
            rootEntityProperties = descriptor.getObjectMapper().readValue(rawContent, HashMap.class);
        } catch (Throwable t) {
            throw new ParseException("Impossible to parse descriptor raw cantent", t);
        }
        Map infoObjectProperties = rootEntityProperties.get("info");
        
        parsedContent.getInfo().setEntityType(EntityType.dataproduct.toString()); 
        infoObjectProperties.put("entityType", EntityType.dataproduct.toString());

        fqn = parsedContent.getInfo().getFullyQualifiedName();
        uuid = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
        parsedContent.getInfo().setDataProductId(uuid);
        infoObjectProperties.put("id", uuid);

        rootEntityProperties.put("info", infoObjectProperties);
        try {
            parsedContent.setRawContent(descriptor.getObjectMapper().writeValueAsString(rootEntityProperties));
        } catch (Throwable t) {
            throw new ParseException("Impossible serialize descriptor", t);
        }

    }

    private void addReadOnlyPropertiesToComponents(List<? extends ComponentResource> components, EntityType entityType) throws ParseException  {
        String fqn, uuid;
        for(ComponentResource component : components) {
            Map componentProperties;
            try {
                componentProperties = descriptor.getObjectMapper().readValue(component.getRawContent(), HashMap.class);
            } catch (Throwable t) {
                throw new ParseException("Impossible to parse component raw cantent", t);
            }
            
            component.setEntityType(entityType);
            componentProperties.put("entityType", entityType);
            
            fqn = (String)component.getFullyQualifiedName();
            uuid = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
            component.setId(uuid);
            componentProperties.put("id", uuid);
            
            try {
                component.setRawContent(descriptor.getObjectMapper().writeValueAsString(componentProperties));
            } catch (Throwable t) {
                throw new ParseException("Impossible serialize component", t);
            }
        }
    }

    public static void resolve(DataProductDescriptor descriptor) throws ParseException {
        ReadOnlyPropertiesResolver resolver = new ReadOnlyPropertiesResolver(descriptor);
        resolver.resolve();
    }
}
