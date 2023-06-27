package org.opendatamesh.platform.core.dpds.api.openapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opendatamesh.platform.core.dpds.api.ApiParser;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionEndpointDPDS;
import org.opendatamesh.platform.core.dpds.parser.UriFetcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OpenApiParser extends ApiParser {

    public OpenApiParser(URI baseUri) {
        super(baseUri);
    }

    @Override
    protected List<ApiDefinitionEndpointDPDS> extractEndpoints(String rawContent, String mediaType)
            throws ParseException, FetchException {
        List<ApiDefinitionEndpointDPDS> endpoints = new ArrayList<ApiDefinitionEndpointDPDS>();

        ObjectMapper mapper = getObjectMapper(mediaType);
        if (mapper == null) {
            throw new ParseException("Impossible to parse api definition encoded in [" + mediaType + "]");
        }

        try {
            ObjectNode apiNode = (ObjectNode) mapper.readTree(rawContent);
            if (!apiNode.has("openapi"))
                return null;
            if (apiNode.get("paths") != null) {
                ObjectNode paths = (ObjectNode) apiNode.get("paths");
                Iterator<String> pathNames = paths.fieldNames();
                int i = 0;
                while (pathNames.hasNext()) {
                    ObjectNode path = (ObjectNode) paths.get(pathNames.next());
                    ObjectNode getOperation = (ObjectNode) path.get("get");
                    if (getOperation == null)
                        continue;
                    ObjectNode responses = (ObjectNode) getOperation.get("responses");
                    if (responses == null)
                        continue;
                    ObjectNode response = (ObjectNode) responses.get("200");
                    if (response == null)
                        continue;
                    ObjectNode schema = (ObjectNode) response.get("schema");

                    ApiDefinitionEndpointDPDS endpoint;
                    String name = null, schemaMediaType = null, outputMediaType = null, operationSchema = null;

                    if (getOperation.get("operationId") != null) {
                        name = getOperation.get("operationId").asText();
                    } else {
                        name = "endpoint-" + (i + 1);
                    }

                    if (schema.get("$ref") != null) {
                        UriFetcher fetcher = new UriFetcher(baseUri);
                        String schemaRef = schema.get("$ref").asText();
                        operationSchema = fetcher.fetch(new URI(schemaRef));
                        if (schemaRef.endsWith(".yaml") || schemaRef.endsWith(".yaml")) {
                            schemaMediaType = "application/yaml";
                        } else if (schemaRef.endsWith(".json")) {
                            schemaMediaType = "application/json";
                        } else {
                            schemaMediaType = mediaType;
                        }
                    } else {
                        operationSchema = schema.toPrettyString();
                        schemaMediaType = mediaType;
                    }

                    if (getOperation.get("produces") != null) {
                        outputMediaType = getOperation.get("produces").asText();
                    } else {
                        outputMediaType = "application/json";
                    }
            
                    endpoint = new ApiDefinitionEndpointDPDS();
                    endpoint.setName(name);
                    ApiDefinitionEndpointDPDS.Schema s = new ApiDefinitionEndpointDPDS.Schema();
                    s.setMediaType(schemaMediaType);
                    s.setContent(operationSchema);
                    endpoint.setSchema(s);
                    endpoints.add(endpoint);

                    i++;

                }
            }
        } catch (JsonProcessingException e) {
            throw new ParseException("Impossible to parse api definition", e);
        } catch (URISyntaxException e) {
            throw new ParseException("Impossible to parse api definition", e);
        }

        return endpoints;
    }

}
