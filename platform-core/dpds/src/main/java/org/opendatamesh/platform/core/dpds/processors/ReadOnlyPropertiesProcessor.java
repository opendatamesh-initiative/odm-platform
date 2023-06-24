package org.opendatamesh.platform.core.dpds.processors;

import java.util.List;
import java.util.UUID;

import org.opendatamesh.platform.core.dpds.DataProductVersionSource;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.serde.DataProductVersionSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReadOnlyPropertiesProcessor implements PropertiesProcessor {
    DataProductVersionDPDS dataProductVersion;
    DataProductVersionSource source;
    ObjectMapper mapper;

    public ReadOnlyPropertiesProcessor(DataProductVersionDPDS dataProductVersionRes, DataProductVersionSource source) {
        this.dataProductVersion = dataProductVersionRes;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;

    }

    @Override
    public void process() throws ParseException {
       
        DataProductVersionDPDS parsedContent = dataProductVersion;

        addReadOnlyPropertiesToInfo();
        
        
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getInputPorts(), EntityTypeDPDS.inputport);
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getOutputPorts(), EntityTypeDPDS.outputport);
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getDiscoveryPorts(), EntityTypeDPDS.discoveryport);
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getObservabilityPorts(), EntityTypeDPDS.observabilityport);
        addReadOnlyPropertiesToComponents(parsedContent.getInterfaceComponents().getControlPorts(), EntityTypeDPDS.controlport);

        addReadOnlyPropertiesToComponents(parsedContent.getInternalComponents().getApplicationComponents(), EntityTypeDPDS.application);
        addReadOnlyPropertiesToComponents(parsedContent.getInternalComponents().getInfrastructuralComponents(), EntityTypeDPDS.infrastructure);
        
    }

    private void addReadOnlyPropertiesToInfo() throws ParseException {
        String fqn, uuid;

        String rawContent = dataProductVersion.getRawContent();
        ObjectNode rootNode = null;
        try {
            rootNode = (ObjectNode)mapper.readTree(rawContent);
        } catch (Throwable t) {
            throw new ParseException("Impossible to parse descriptor raw cantent", t);
        }
        ObjectNode infoNode = (ObjectNode)rootNode.get("info");

        // Set field "entityType"
        dataProductVersion.getInfo().setEntityType(EntityTypeDPDS.dataproduct.toString()); 
        infoNode.put("entityType", EntityTypeDPDS.dataproduct.toString());

        // Set field "id"
        fqn = dataProductVersion.getInfo().getFullyQualifiedName();
        uuid = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
        dataProductVersion.getInfo().setDataProductId(uuid);
        infoNode.put("id", uuid);

        rootNode.set("info", infoNode);
        try {
            dataProductVersion.setRawContent(mapper.writeValueAsString(rootNode));
        } catch (Throwable t) {
            throw new ParseException("Impossible serialize descriptor", t);
        }
    }

    private void addReadOnlyPropertiesToComponents(List<? extends ComponentDPDS> components, EntityTypeDPDS entityType) throws ParseException  {
        String fqn, uuid;

        for(ComponentDPDS component : components) {
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
    

    public static void process(DataProductVersionDPDS dataProductVersionRes, DataProductVersionSource source) throws ParseException {
        ReadOnlyPropertiesProcessor resolver = new ReadOnlyPropertiesProcessor(dataProductVersionRes, source);
        resolver.process();
    }
}
