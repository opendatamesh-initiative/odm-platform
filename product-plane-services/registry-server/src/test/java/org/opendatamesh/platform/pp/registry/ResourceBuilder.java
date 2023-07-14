package org.opendatamesh.platform.pp.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceBuilder {

    ObjectMapper mapper;

    public ResourceBuilder() {
        mapper = new ObjectMapper();
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

    
}
