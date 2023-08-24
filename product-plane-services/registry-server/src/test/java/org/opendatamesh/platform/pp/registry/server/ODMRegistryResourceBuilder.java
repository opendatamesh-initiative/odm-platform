package org.opendatamesh.platform.pp.registry.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.DefinitionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ODMRegistryResourceBuilder {

    ObjectMapper mapper;

    // FIXME cache does not work because the spring's context is re-initialized after each test
    Map<String, String> fileCache;

    private static final Logger logger = LoggerFactory.getLogger(ODMRegistryResourceBuilder.class);

    public ODMRegistryResourceBuilder() {
        mapper = ObjectMapperFactory.JSON_MAPPER;
        fileCache = new HashMap<String, String>();
    }
    
    public String readResourceFromFile(String filePath) throws IOException {
        String fileContent = null;

        Objects.requireNonNull(filePath, "Parameter [filePath] cannot be null");

        if(fileCache.containsKey(filePath)) {
            fileContent = fileCache.get(filePath);
        } else {
            fileContent = Files.readString(Paths.get(filePath));
            fileCache.put(filePath, fileContent);
            logger.debug("File [" + filePath + "] succesfully read");
        }
        
        return fileContent;
    }

    public <T> T readResourceFromFile(String filePath, Class<T> resourceType) throws IOException {
        String fileContent = readResourceFromFile(filePath);
        return mapper.readValue(fileContent, resourceType);
    }

    public DataProductResource buildDataProduct(String fqn, String domain, String descriptione) {
        return buildDataProduct(null, fqn, domain, descriptione);
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
