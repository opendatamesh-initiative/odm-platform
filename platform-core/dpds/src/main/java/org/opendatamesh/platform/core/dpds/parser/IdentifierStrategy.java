package org.opendatamesh.platform.core.dpds.parser;

import org.apache.commons.lang3.StringUtils;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.core.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.info.InfoDPDS;

import java.util.Objects;
import java.util.UUID;

public class IdentifierStrategy {

    private final String organization = "org.opendatamesh";

    public static IdentifierStrategy DEFUALT = new IdentifierStrategy();

    private IdentifierStrategy() {
    }

    public String getId(String fqn) {
        String id = null;
        if (StringUtils.isNotBlank(fqn)) {
            id = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
        } else {
            throw new RuntimeException("Fully qualified name is empty");
        }
        return id;
    }

    /**
     * 
     * @param descriptor
     * @return the prdouct fqn
     */
    public String getFqn(DataProductVersionDPDS descriptor) {
        String fqn = null;

        InfoDPDS info = descriptor.getInfo();

        if (info.getEntityType() == null)
            throw new RuntimeException("Impossible to define fqn of product because the entity type is null");
        if (StringUtils.isBlank(info.getName()))
            throw new RuntimeException("Impossible to define fqn of product because the name is empty");

        fqn = String.format(
                "urn:%s:%s:%s",
                organization,
                info.getEntityType() + "s", // we can do better than this
                info.getName());

        return fqn;
    }

    /**
     * 
     * @param descriptor
     * @return the component fqn
     */
    public String getFqn(DataProductVersionDPDS descriptor, ComponentDPDS component) {
        String fqn = null;

        if (component.getType() == null)
            throw new RuntimeException(
                    "Impossible to define fqn of component because the entity type is empty: " + component);

        if (!component.getType().isComponent())
            throw new RuntimeException(
                    "Impossible to define fqn of component because its type [" + component.getType()
                            + "] is not a component type: " + component);

        if (component.getType().isExternalComponent()) {
            if (StringUtils.isBlank(component.getName())) {
                String definition = null;
                if (component.getType() == EntityTypeDPDS.API
                        || component.getType() == EntityTypeDPDS.TEMPLATE) {
                    StandardDefinitionDPDS stdDef = (StandardDefinitionDPDS) component;
                    definition = stdDef.getDefinition() != null ? stdDef.getDefinition().getRawContent() : null;
                    if (StringUtils.isBlank(definition)) {
                        throw new RuntimeException(
                                "If definition name is empty raw content must be valorized");
                    }
                    String name = UUID.nameUUIDFromBytes(definition.getBytes()).toString();
                    stdDef.setName(name);
                } else {
                    throw new RuntimeException(
                            "Impossible to define fqn of component because the name is empty: " + component);
                }
            }

            if (StringUtils.isBlank(component.getVersion())) {
                component.setVersion("1.0.0");
            }

            fqn = String.format(
                    "urn:%s:%s:%s:%s",
                    organization,
                    component.getType().collectionName(),
                    component.getName(),
                    component.getVersion());
        } else {
            if (StringUtils.isBlank(descriptor.getInfo().getFullyQualifiedName()))
                throw new RuntimeException(
                        "Impossible to define fqn of component because the fqn of parent product is emty"
                                + component);
            if (StringUtils.isBlank(descriptor.getInfo().getVersionNumber()))
                throw new RuntimeException(
                        "Impossible to define fqn of component because the version number of parent version is empty: "
                                + component);

            if (StringUtils.isBlank(component.getName()))
                throw new RuntimeException(
                        "Impossible to define fqn of component because the name is empty: " + component);
            if (StringUtils.isBlank(component.getVersion()))
                throw new RuntimeException(
                        "Impossible to define fqn of component because the version type is empty: " + component);

            fqn = String.format(
                    "%s:%s:%s:%s:%s",
                    descriptor.getInfo().getFullyQualifiedName(), // product fqn
                    descriptor.getInfo().getVersionNumber(), // descriptor version number
                    component.getType().collectionName(),
                    component.getName(),
                    component.getVersion());
        }

        return fqn;
    }

    public String getExternalComponentFqn(ComponentDPDS component) {
        String fqn = null;

        Objects.requireNonNull(component, "Parameter [component] cannot be null");
        if (!component.getType().isExternalComponent()) {
            throw new IllegalArgumentException("Component must be external");
        }

        if (StringUtils.isBlank(component.getName())) {
            String definition = null;
            if (component.getType() == EntityTypeDPDS.API
                    || component.getType() == EntityTypeDPDS.TEMPLATE) {
                StandardDefinitionDPDS stdDef = (StandardDefinitionDPDS) component;
                definition = stdDef.getDefinition() != null ? stdDef.getDefinition().getRawContent() : null;
                if (StringUtils.isBlank(definition)) {
                    throw new RuntimeException(
                            "If definition name is empty raw content must be valorized");
                }
                String name = UUID.nameUUIDFromBytes(definition.getBytes()).toString();
                stdDef.setName(name);
            } else {
                throw new RuntimeException(
                        "Impossible to define fqn of component because the name is empty: " + component);
            }
        }

        if (StringUtils.isBlank(component.getVersion())) {
            component.setVersion("1.0.0");
        }

        fqn = getExternalComponentFqn(
            component.getType(),
            component.getName(),
            component.getVersion()
        );
        
        return fqn;
    }

    public String getExternalComponentFqn(
        EntityTypeDPDS type, 
        String name, 
        String version) {
        
        String fqn = String.format(
                "urn:%s:%s:%s:%s",
                organization,
                type.collectionName(),
                name,
                version);

        return fqn;
    }
}