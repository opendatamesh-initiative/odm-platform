package org.opendatamesh.platform.pp.devops.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ODMDevOpsResourceBuilder {

    ObjectMapper mapper;

    // FIXME cache does not work because the spring's context is re-initialized after each test
    Map<String, String> fileCache;

    private static final Logger logger = LoggerFactory.getLogger(ODMDevOpsResourceBuilder.class);

    public ODMDevOpsResourceBuilder() {
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

    public ActivityResource buildActivity(
            String dataProductId, String dataProductVersion, String type) {
        return buildActivity(null, dataProductId, dataProductVersion, type);
    }

    public ActivityResource buildActivity(Long id,
            String dataProductId, String dataProductVersion, String type)  {
       
        ActivityResource activityRes = null;

        activityRes = new ActivityResource();
        activityRes.setId(id);
        activityRes.setDataProductId(dataProductId);
        activityRes.setDataProductVersion(dataProductVersion);
        activityRes.setType(type);

        return activityRes;
    }
    
}
