package org.opendatamesh.platform.pp.registry.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.parser.location.GitLocation;
import org.opendatamesh.platform.pp.registry.api.resources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ODMRegistryResourceBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ODMRegistryResourceBuilder.class);

    public ODMRegistryResourceBuilder() {
    }
    
    public DataProductResource buildDataProduct(String fqn, String domain, String descriptione) {
        return buildDataProduct(null, fqn, domain, descriptione);
    }

    public DataProductResource buildTestDataProduct() {
        return buildDataProduct(
            "f350cab5-992b-32f7-9c90-79bca1bf10be", 
            "urn:org.opendatamesh:dataproducts:dpdCore", 
            "Test Domain",
            "This is test product #1");
    }
    public DataProductResource buildDataProduct(String id,
            String fqn, String domain, String description)  {
       
        DataProductResource dataProductRes = null;

        dataProductRes = new DataProductResource();
        dataProductRes.setId(id);
        dataProductRes.setFullyQualifiedName(fqn);
        dataProductRes.setDomain(domain);
        dataProductRes.setDescription(description);
        
        return dataProductRes;
    }

    public ExternalComponentResource buildTestApi() {
        return buildDefinition(
            "b461ea5e-de52-3509-a297-ebaac9c49e67", 
            "urn:org.opendatamesh:apis:api-1:1.0.0", 
            EntityTypeDPDS.API.propertyValue(), 
            "api-1", "1.0.0", 
            "Api 1", "Test Api", 
            "custom-spec", "1.0", 
            "plain/text", "api definition");
    }

    public ExternalComponentResource buildTestTemplate() {
        return buildDefinition(
            "d3fdbe13-eac8-32b8-b884-0f5a3ccd16e8", 
            "urn:org.opendatamesh:templates:template-1:1.0.0", 
            EntityTypeDPDS.TEMPLATE.propertyValue(), 
            "template-1", "1.0.0", 
            "Template 1", "Test Template", 
            "custom-spec", "1.0", 
            "plain/text", "template definition");
    }

    public ExternalComponentResource buildDefinition(
        String id, 
        String fqn, 
        String entityType, 
        String name, 
        String version, 
        String displayName, 
        String description, 
        String specification, 
        String specificationVersion, 
        String definitionMediaType, 
        String definition) {
        ExternalComponentResource definitionRes;

        definitionRes = new ExternalComponentResource();
        definitionRes.setId(id);
        definitionRes.setFullyQualifiedName(fqn);
        definitionRes.setEntityType(entityType);
        definitionRes.setName(name);
        definitionRes.setVersion(version);
        definitionRes.setDisplayName(displayName);
        definitionRes.setDescription(description);
        definitionRes.setSpecification(specification);
        definitionRes.setSpecificationVersion(specificationVersion);
        definitionRes.setDefinitionMediaType(definitionMediaType);
        definitionRes.setDefinition(definition);

        return definitionRes;
    }

    public DomainResource buildDomain(String fqn, String name,
                                      String displayName, String summary, String description) throws IOException {

        return buildDomain(null, fqn, name, displayName, summary, description);
    }

    public DomainResource buildDomain(String fqn) throws IOException {return buildDomain(fqn, null, null, null, null);}

    public DomainResource buildDomain(String id, String fqn, String name,
                                      String displayName, String summary, String description) throws IOException {

        DomainResource domainResource = null;

        domainResource = new DomainResource();
        domainResource.setId(id);
        domainResource.setFullyQualifiedName(fqn);
        domainResource.setName(name);
        domainResource.setDisplayName(displayName);
        domainResource.setDescription(description);
        domainResource.setSummary(summary);

        return domainResource;
    }

    public OwnerResource buildOwner(String id, String name) throws IOException {

        OwnerResource ownerResource = null;

        ownerResource = new OwnerResource();
        ownerResource.setId(id);
        ownerResource.setName(name);

        return ownerResource;
    }

}
