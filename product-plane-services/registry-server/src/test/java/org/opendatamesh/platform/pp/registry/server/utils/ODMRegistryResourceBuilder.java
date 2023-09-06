package org.opendatamesh.platform.pp.registry.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.api.resources.DomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ODMRegistryResourceBuilder {

    ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(ODMRegistryResourceBuilder.class);

    public ODMRegistryResourceBuilder() {
        mapper = ObjectMapperFactory.JSON_MAPPER;
    }
    
    public String getContent(ODMRegistryResources resource) throws IOException {
        String fileContent = null;

        Objects.requireNonNull(resource, "Parameter [resource] cannot be null");

        fileContent = Files.readString(getPath(resource));
        logger.debug("File [" + resource.getPath() + "] succesfully read");
        
        return fileContent;

        /* 
        String content = "";

       try {
            Path p = Path.of(path);
            if (Files.exists(p)) {
                content = FileUtils.readFileToString(p.toFile(), StandardCharsets.UTF_8.displayName());
            } else {
                content = loadFileFromClasspath(path);
            }
        } catch (IOException e) {
            throw new IOException("Impossible to get resource [" + path + "] content", e);
        }

        return content;
        */
    }

    private Path getPath(ODMRegistryResources resource) {
        //return Paths.get(resource.getPath());
      
        ClassLoader cl = getClass().getClassLoader();
        String absoluteFilePath = cl.getResource(resource.getPath()).getFile();
        return Path.of(absoluteFilePath);

    }

    public <T> T readResourceFromFile(ODMRegistryResources resource, Class<T> resourceType) throws IOException {
        String fileContent = getContent(resource);
        return mapper.readValue(fileContent, resourceType);
    }

    

    private String loadFileFromClasspath(String location) throws IOException {

        String content = "";

        String file = FilenameUtils.separatorsToUnix(location);

        InputStream inputStream = ODMRegistryResources.class.getResourceAsStream(file);

        if (inputStream == null) {
            inputStream = ODMRegistryResources.class.getClassLoader().getResourceAsStream(file);
        }

        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(file);
        }

        if (inputStream != null) {
            try {
                content = IOUtils.toString(inputStream, Charset.forName(StandardCharsets.UTF_8.displayName()));
            } catch (IOException e) {
                throw new RuntimeException("Could not read " + file + " from the classpath", e);
            }
        } else {
            throw new IOException("Impossible to get resource [" + location + "] content from classpath");
        }

        return content;
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
}
