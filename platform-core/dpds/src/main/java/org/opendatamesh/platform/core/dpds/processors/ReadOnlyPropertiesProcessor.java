package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReadOnlyPropertiesProcessor implements PropertiesProcessor {
    ParseContext context;
    ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(ReadOnlyPropertiesProcessor.class);

    public ReadOnlyPropertiesProcessor(ParseContext context) {
        this.context = context;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;

    }

    @Override
    public void process() throws ParseException {

        DataProductVersionDPDS descriptor = context.getResult().getDescriptorDocument();

        addReadOnlyPropertiesToInfo();

        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getInputPorts(),
                EntityTypeDPDS.inputport);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getOutputPorts(),
                EntityTypeDPDS.outputport);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getDiscoveryPorts(),
                EntityTypeDPDS.discoveryport);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getObservabilityPorts(),
                EntityTypeDPDS.observabilityport);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getControlPorts(),
                EntityTypeDPDS.controlport);

        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInternalComponents().getApplicationComponents(),
                EntityTypeDPDS.application);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInternalComponents().getInfrastructuralComponents(),
                EntityTypeDPDS.infrastructure);

    }

    private void addReadOnlyPropertiesToInfo() throws ParseException {
        String entityType, fqn, uuid;

        DataProductVersionDPDS descriptor = context.getResult().getDescriptorDocument();
        String rawContent = descriptor.getRawContent();
        ObjectNode rootNode = null;
        try {
            rootNode = (ObjectNode) mapper.readTree(rawContent);
        } catch (Throwable t) {
            throw new ParseException("Impossible to parse descriptor raw cantent", t);
        }
        ObjectNode infoNode = (ObjectNode) rootNode.get("info");

        // Rewrite "entityType"
        entityType = EntityTypeDPDS.dataproduct.toString();
        if (!entityType.equals(descriptor.getInfo().getEntityType())) {
            if (context.getOptions().isValidateReadOnlyProperties()) {
                throw new ParseException("Invalid value [" + descriptor.getInfo().getEntityType()
                        + "] for field entityType in infoObject. Expected [" + entityType + "]");
            } else {
                logger.warn("Invalid value [" + descriptor.getInfo().getEntityType()
                        + "] for field entityType in infoObject. Expected [" + entityType + "]");
            }
        }
        if (context.getOptions().isRewriteEntityType()) {
            descriptor.getInfo().setEntityType(entityType);
            infoNode.put("entityType", entityType);
        }

        // Rewrite "fqn"
        fqn = context.getOptions().getIdentifierStrategy().getFqn(descriptor);
        if (!fqn.equals(descriptor.getInfo().getFullyQualifiedName())) {
            if (context.getOptions().isValidateReadOnlyProperties()) {
                throw new ParseException("Invalid value [" + descriptor.getInfo().getFullyQualifiedName()
                        + "] for field fullyQualifiedName in infoObject. Expected [" + fqn + "]");
            } else {
                logger.warn("Invalid value [" + descriptor.getInfo().getFullyQualifiedName()
                        + "] for field fullyQualifiedName in infoObject. Expected [" + fqn + "]");
            }
        }
        if (context.getOptions().isRewriteFqn()) {
            descriptor.getInfo().setFullyQualifiedName(fqn);
            infoNode.put("fullyQualifiedName", fqn);
        }

        // Rewrite "id"
        fqn = descriptor.getInfo().getFullyQualifiedName();
        uuid = context.getOptions().getIdentifierStrategy().getId(fqn);
        if (!uuid.equals(descriptor.getInfo().getDataProductId())) {
            if (context.getOptions().isValidateReadOnlyProperties()) {
                throw new ParseException("Invalid value [" + descriptor.getInfo().getDataProductId()
                        + "] for field id in infoObject. Expected [" + uuid + "]");
            } else {
                logger.warn("Invalid value [" + descriptor.getInfo().getDataProductId()
                        + "] for field id in infoObject. Expected [" + uuid + "]");
            }
        }
        if (context.getOptions().isRewriteId()) {
            descriptor.getInfo().setDataProductId(uuid);
            infoNode.put("id", uuid);
        }

        rootNode.set("info", infoNode);
        try {
            descriptor.setRawContent(mapper.writeValueAsString(rootNode));
        } catch (Throwable t) {
            throw new ParseException("Impossible serialize descriptor", t);
        }
    }

    private void addReadOnlyPropertiesToComponents(
        DataProductVersionDPDS descriptor,    
        List<? extends ComponentDPDS> components, 
        EntityTypeDPDS entityType)
    throws ParseException {
        
        String fqn, uuid;

        for (ComponentDPDS component : components) {
            ObjectNode componentNode;
            try {
                componentNode = (ObjectNode) mapper.readTree(component.getRawContent());
            } catch (Throwable t) {
                throw new ParseException("Impossible to parse component raw cantent", t);
            }

            // Rewrite "entityType"
            if (!entityType.equals(component.getEntityType())) {
                if (context.getOptions().isValidateReadOnlyProperties()) {
                    throw new ParseException("Invalid value [" + component.getEntityType()
                            + "] for field entityType in component [" + component.getName() + "]. Expected [" + entityType + "]");
                } else {
                    logger.warn("Invalid value [" + component.getEntityType()
                            + "] for field entityType in component [" + component.getName() + "]. Expected [" + entityType + "]");
                }
            }
            if (context.getOptions().isRewriteEntityType()) {
               component.setEntityType(entityType);
                componentNode.put("entityType", entityType.toString());
            }

            // Rewrite "fqn"
            fqn = context.getOptions().getIdentifierStrategy().getFqn(descriptor, component);
            if (!fqn.equals(component.getFullyQualifiedName())) {
                if (context.getOptions().isValidateReadOnlyProperties()) {
                    throw new ParseException("Invalid value [" + component.getFullyQualifiedName()
                            + "] for field fullyQualifiedName in component [" + component.getName() + "]. Expected [" + fqn + "]");
                } else {
                    logger.warn("Invalid value [" + component.getFullyQualifiedName()
                            + "] for field fullyQualifiedName in component [" + component.getName() + "]. Expected [" + fqn + "]");
                }
            }
            if (context.getOptions().isRewriteFqn()) {
                component.setFullyQualifiedName(fqn);
                componentNode.put("fullyQualifiedName", fqn);
            }

            // Rewrite "id"
            fqn = component.getFullyQualifiedName();
            uuid = context.getOptions().getIdentifierStrategy().getId(fqn);
            if (!uuid.equals(component.getId())) {
                if (context.getOptions().isValidateReadOnlyProperties()) {
                    throw new ParseException("Invalid value [" + component.getId()
                            + "] for field id in component [" + component.getName() + "]. Expected [" + uuid + "]");
                } else {
                    logger.warn("Invalid value [" + component.getId()
                            + "] for field id in component [" + component.getName() + "]. Expected [" + uuid + "]");
                }
            }
            if (context.getOptions().isRewriteId()) {
                component.setId(uuid);
                componentNode.put("id", uuid);
            }


            try {
                component.setRawContent(mapper.writeValueAsString(componentNode));
            } catch (Throwable t) {
                throw new ParseException("Impossible serialize component", t);
            }

        }
    }

    public static void process(ParseContext context) throws ParseException {
        ReadOnlyPropertiesProcessor resolver = new ReadOnlyPropertiesProcessor(context);
        resolver.process();
    }
}
