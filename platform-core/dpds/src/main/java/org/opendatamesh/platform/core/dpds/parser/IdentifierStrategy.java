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
