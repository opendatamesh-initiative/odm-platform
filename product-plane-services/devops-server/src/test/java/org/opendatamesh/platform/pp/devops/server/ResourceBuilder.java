package org.opendatamesh.platform.pp.devops.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;

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

    public ActivityResource buildActivity(
            String dataProductId, String dataProductVersion, String type) throws IOException {
        return buildActivity(null, dataProductId, dataProductVersion, type);
    }

    public ActivityResource buildActivity(Long id,
            String dataProductId, String dataProductVersion, String type) throws IOException {
       
        ActivityResource activityRes = null;

        activityRes = new ActivityResource();
        activityRes.setId(id);
        activityRes.setDataProductId(dataProductId);
        activityRes.setDataProductVersion(dataProductVersion);
        activityRes.setType(type);

        return activityRes;
    }
    
}
