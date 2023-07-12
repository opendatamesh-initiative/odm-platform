package org.opendatamesh.platform.core.dpds.parser;

import org.apache.commons.lang3.StringUtils;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfoDPDS;

import java.util.UUID;


public class IdentifierStrategy {

    private final String organization = "org.opendatamesh";

    public static IdentifierStrategy DEFUALT = new IdentifierStrategy();

    private IdentifierStrategy() {}

    public String getId(String fqn) {
        String id = null;
        if(StringUtils.isNotBlank(fqn)) {
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

        if (info.getEntityType() == null) throw new RuntimeException("Impossible to define fqn of product because the entity type is null");
        if (StringUtils.isBlank(info.getName())) throw new RuntimeException("Impossible to define fqn of product because the name is empty");

        fqn = String.format(
            "urn:%s:%s:%s", 
            organization,
            info.getEntityType(),
            info.getName()
        );
        
        return fqn;
    }

    /**
     * 
     * @param descriptor
     * @return the component fqn
     */
    public String getFqn(DataProductVersionDPDS descriptor, ComponentDPDS component) {
        String fqn = null;
        
        if (StringUtils.isBlank(descriptor.getInfo().getFullyQualifiedName())) throw new RuntimeException("Impossible to define fqn of component because the fqn of parent product is emty: " + component);
        if (StringUtils.isBlank(descriptor.getInfo().getVersionNumber())) throw new RuntimeException("Impossible to define fqn of component because the version number of parent version is empty: " + component);
        if (component.getEntityType() == null) throw new RuntimeException("Impossible to define fqn of component because the entity type is empty: " + component);
        if (StringUtils.isBlank(component.getName())) throw new RuntimeException("Impossible to define fqn of component because the name is empty: " + component);
        if (StringUtils.isBlank(component.getVersion())) throw new RuntimeException("Impossible to define fqn of component because the version type is empty: " + component);
        fqn = String.format(
            "%s:%s:%s:%s:%s", 
            descriptor.getInfo().getFullyQualifiedName(), // product fqn
            descriptor.getInfo().getVersionNumber(), // descriptor version number
            component.getEntityType(),
            component.getName(),
            component.getVersion()
        );
        
        return fqn;
    }
}
