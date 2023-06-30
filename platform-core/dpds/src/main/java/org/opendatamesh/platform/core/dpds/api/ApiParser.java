package org.opendatamesh.platform.core.dpds.api;

import java.net.URI;
import java.util.List;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.parser.location.UriFetcher;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionEndpointDPDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ApiParser {
    protected URI baseUri;
    protected UriFetcher fetcher;

    private static final Logger logger = LoggerFactory.getLogger(ApiParser.class);

    
    public ApiParser(URI baseUri) {
        this.baseUri = baseUri;
        this.fetcher = new UriFetcher();
    }

    public ApiParser(URI baseUri, UriFetcher fetcher) {
        this.baseUri = baseUri;
        this.fetcher = fetcher;
    }

    public ApiDefinitionReferenceDPDS parse(String rawContent, String mediaType) throws ParseException, FetchException {
        ApiDefinitionReferenceDPDS api = new ApiDefinitionReferenceDPDS();
        api.setBaseUri(baseUri);
        api.setRawContent(rawContent);
        api.setEndpoints(extractEndpoints(rawContent, resolveMediaType(mediaType)));
        return api;
    }

    protected String resolveMediaType(String mediaType ) {

        if(mediaType == null) {
            logger.warn("Media type is not specified. Defualt media type [application/json] will be used");
            return "application/json";
        }

        mediaType = mediaType.toLowerCase().trim();
        if(mediaType.equalsIgnoreCase("text/json")) {
            logger.warn("Media type [text/json] is incorrect. Media type [application/json] will be used");
            mediaType = "application/json";
        } else if(mediaType.endsWith("+json")) {
             mediaType = "application/json";
        } else if(mediaType.equalsIgnoreCase("text/yaml")) {
            logger.warn("Media type [text/yaml] is incorrect. Media type [application/yaml] will be used");
            mediaType = "application/yaml";
        } else if(mediaType.endsWith("+yaml")) {
             mediaType = "application/yaml";
        }

        return mediaType;
    }

    protected ObjectMapper getObjectMapper(String mediaType) {
        ObjectMapper mapper = null;
        if("application/json".equals(mediaType)) {
            mapper = ObjectMapperFactory.JSON_MAPPER;
        } else if("application/yaml".equals(mediaType)) {
            mapper = ObjectMapperFactory.YAML_MAPPER;
        }
        return mapper;
    }

    protected abstract List<ApiDefinitionEndpointDPDS> extractEndpoints(String rawContent, String mediaType) throws ParseException, FetchException;


}
