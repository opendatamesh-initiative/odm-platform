package org.opendatamesh.platform.pp.registry.api.parser;

import org.apache.commons.lang3.StringUtils;
import org.opendatamesh.platform.pp.registry.api.resources.DomainResource;

import java.util.UUID;


public class DomainIdentifierStrategy{

    private final String organization = "org.opendatamesh";

    public static DomainIdentifierStrategy DOMAIN_STRATEGY = new DomainIdentifierStrategy();

    private DomainIdentifierStrategy() { }

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
     * @param domainResource
     * @return the domain fqn
     */
    public String getFqn(DomainResource domainResource) {
        String fqn = null;

        if (StringUtils.isBlank(domainResource.getName())) throw new RuntimeException("Impossible to define fqn of Domain because the name is empty");

        fqn = String.format(
            "urn:odmp:%s:domains:%s",
            organization,
            domainResource.getName()
        );
        
        return fqn;
    }

}
