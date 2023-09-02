package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReadOnlyPropertiesProcessor implements PropertiesProcessor {
    
    ParseContext context;

    private static final Logger logger = LoggerFactory.getLogger(ReadOnlyPropertiesProcessor.class);

    public ReadOnlyPropertiesProcessor(ParseContext context) {
        this.context = context;
    }

    @Override
    public void process() throws DeserializationException {

        DataProductVersionDPDS descriptor = context.getResult().getDescriptorDocument();

        addReadOnlyPropertiesToInfo();

        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getInputPorts(),
                EntityTypeDPDS.INPUTPORT);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getOutputPorts(),
                EntityTypeDPDS.OUTPUTPORT);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getDiscoveryPorts(),
                EntityTypeDPDS.DISCOVERYPORT);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getObservabilityPorts(),
                EntityTypeDPDS.OBSERVABILITYPORT);
        addReadOnlyPropertiesToComponents(descriptor, descriptor.getInterfaceComponents().getControlPorts(),
                EntityTypeDPDS.CONTROLPORT);

        if (descriptor.getInternalComponents() != null) {
            addReadOnlyPropertiesToComponents(descriptor, descriptor.getInternalComponents().getApplicationComponents(),
                    EntityTypeDPDS.APPLICATION);
            addReadOnlyPropertiesToComponents(descriptor,
                    descriptor.getInternalComponents().getInfrastructuralComponents(),
                    EntityTypeDPDS.INFRASTRUCTURE);

        }

    }

    private void addReadOnlyPropertiesToInfo() throws DeserializationException {
        String entityType, fqn, uuid;

        DataProductVersionDPDS descriptor = context.getResult().getDescriptorDocument();
        String rawContent = descriptor.getRawContent();
        ObjectNode rootNode = null;
        try {
            rootNode = (ObjectNode) context.getMapper().readTree(rawContent);
        } catch (Throwable t) {
            throw new DeserializationException("Impossible to parse descriptor raw cantent", t);
        }
        ObjectNode infoNode = (ObjectNode) rootNode.get("info");

        // Rewrite "entityType"
        entityType = EntityTypeDPDS.DATAPRODUCT.propertyValue();
        if (!entityType.equals(descriptor.getInfo().getEntityType())) {
            if (context.getOptions().isValidateReadOnlyProperties()) {
                throw new DeserializationException("Invalid value [" + descriptor.getInfo().getEntityType()
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
        try {
            fqn = context.getOptions().getIdentifierStrategy().getFqn(descriptor);
        } catch (Throwable t) {
            throw new DeserializationException("Impossible to calculate data product fqn", t);
        }

        if (!fqn.equals(descriptor.getInfo().getFullyQualifiedName())) {
            if (context.getOptions().isValidateReadOnlyProperties()) {
                throw new DeserializationException("Invalid value [" + descriptor.getInfo().getFullyQualifiedName()
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
                throw new DeserializationException("Invalid value [" + descriptor.getInfo().getDataProductId()
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
            descriptor.setRawContent(context.getMapper().writeValueAsString(rootNode));
        } catch (Throwable t) {
            throw new DeserializationException("Impossible serialize descriptor", t);
        }
    }

    private void addReadOnlyPropertiesToComponents(
            DataProductVersionDPDS descriptor,
            List<? extends ComponentDPDS> components,
            EntityTypeDPDS entityType)
            throws DeserializationException {

        String fqn, uuid;

        for (ComponentDPDS component : components) {
            ObjectNode componentNode;
            try {
                componentNode = (ObjectNode) context.getMapper().readTree(component.getRawContent());
            } catch (Throwable t) {
                throw new DeserializationException("Impossible to parse component raw cantent", t);
            }

            // Rewrite "entityType"
            if (!entityType.equals(component.getEntityType())) {
                if (context.getOptions().isValidateReadOnlyProperties()) {
                    throw new DeserializationException("Invalid value [" + component.getEntityType()
                            + "] for field entityType in component [" + component.getName() + "]. Expected ["
                            + entityType + "]");
                } else {
                    logger.warn("Invalid value [" + component.getEntityType()
                            + "] for field entityType in component [" + component.getName() + "]. Expected ["
                            + entityType + "]");
                }
            }
            if (context.getOptions().isRewriteEntityType()) {
                component.setEntityType(entityType.propertyValue());
                componentNode.put("entityType", entityType.toString());
            }

            // Rewrite "fqn"
            try {
                fqn = context.getOptions().getIdentifierStrategy().getFqn(descriptor, component);
            } catch (Throwable t) {
                throw new DeserializationException("Impossible to calculate component fqn", t);
            }
            if (!fqn.equals(component.getFullyQualifiedName())) {
                if (context.getOptions().isValidateReadOnlyProperties()) {
                    throw new DeserializationException("Invalid value [" + component.getFullyQualifiedName()
                            + "] for field fullyQualifiedName in component [" + component.getName() + "]. Expected ["
                            + fqn + "]");
                } else {
                    logger.warn("Invalid value [" + component.getFullyQualifiedName()
                            + "] for field fullyQualifiedName in component [" + component.getName() + "]. Expected ["
                            + fqn + "]");
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
                    throw new DeserializationException("Invalid value [" + component.getId()
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
                component.setRawContent(context.getMapper().writeValueAsString(componentNode));
            } catch (Throwable t) {
                throw new DeserializationException("Impossible serialize component", t);
            }

        }
    }

    public static void process(ParseContext context) throws DeserializationException {
        ReadOnlyPropertiesProcessor resolver = new ReadOnlyPropertiesProcessor(context);
        resolver.process();
    }
}
