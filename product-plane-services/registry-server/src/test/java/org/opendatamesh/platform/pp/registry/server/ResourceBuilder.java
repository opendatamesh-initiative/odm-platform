package org.opendatamesh.platform.pp.registry.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DomainResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceBuilder {

    ObjectMapper mapper;

    public ResourceBuilder() {
        mapper = ObjectMapperFactory.JSON_MAPPER;
    }
    
    public String readResourceFromFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    public <T> T readResourceFromFile(String filePath, Class<T> resourceType) throws IOException {
        String fileContent = readResourceFromFile(filePath);
        return mapper.readValue(fileContent, resourceType);
    }

    public DataProductResource buildDataProduct(
            String fqn, String domain, String description) throws IOException {
        return buildDataProduct(null, fqn, domain, description);
    }

    public DataProductResource buildDataProduct(String id,
            String fqn, String domain, String description) throws IOException {
       
        DataProductResource dataProductRes = null;

        dataProductRes = new DataProductResource();
        dataProductRes.setId(id);
        dataProductRes.setFullyQualifiedName(fqn);
        dataProductRes.setDescription(description);
        dataProductRes.setDomain(domain);
       
        return dataProductRes;
    }

    public DefinitionResource buildDefinition(String name, String version, String contentMediaType, String content) {
        DefinitionResource definitionRes;

        definitionRes = new DefinitionResource();
        definitionRes.setName(name);
        definitionRes.setVersion(version);
        definitionRes.setContentMediaType(contentMediaType);
        definitionRes.setContent(content);

        return definitionRes;
    }

    public DomainResource buildDomain(String fqn, String name,
                                      String displayName, String summary, String description) throws IOException {

        DomainResource domainResource = null;

        domainResource = new DomainResource();
        domainResource.setFullyQualifiedName(fqn);
        domainResource.setName(name);
        domainResource.setDisplayName(displayName);
        domainResource.setDescription(description);
        domainResource.setSummary(summary);

        return domainResource;
    }

    public DomainResource buildDomain(String fqn) throws IOException {return buildDomain(fqn, null, null, null, null);}
    
}
