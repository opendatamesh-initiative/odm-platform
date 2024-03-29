package org.opendatamesh.platform.core.dpds.api.dsapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.api.ApiParser;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionEndpointDPDS;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class DataStoreApiParser extends ApiParser {
    
    public DataStoreApiParser(URI baseUri) {
        super(baseUri);
    }

    @Override
    protected List<ApiDefinitionEndpointDPDS> extractEndpoints(String rawContent, String mediaType) throws DeserializationException, FetchException {
        List<ApiDefinitionEndpointDPDS> endpoints = new ArrayList<ApiDefinitionEndpointDPDS>();

        if(!"application/json".equalsIgnoreCase(mediaType)) {
            throw new DeserializationException("Impossible to parse api definition encoded in [" + mediaType + "]");
        }

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        try {
            ObjectNode apiNode = (ObjectNode)mapper.readTree(rawContent);
            if(!apiNode.has("datastoreapi")) return null;
            if(!apiNode.at("/schema/tables").isMissingNode()){
                ArrayNode tables = (ArrayNode)apiNode.at("/schema/tables");
                for(int i = 0; i < tables.size(); i++) {
                    ApiDefinitionEndpointDPDS endpoint;
                    String name = null, schemaMediaType = null, tableSchema = null;
                    ObjectNode table = (ObjectNode)tables.get(i);
                    if(table.get("name") != null) {
                        name = table.get("name").asText();
                    } else {
                        name = "endpoint-" + (i+1);
                    }
                    if(!table.at("/definition/$ref").isMissingNode()) {
                        tableSchema = fetcher.fetch(baseUri, new URI(table.at("/definition/$ref").asText()));
                    } else {
                        tableSchema = mapper.writeValueAsString(table.at("/definition"));
                    }

                    if(!table.at("/definition/mediaType").isMissingNode()) {
                        schemaMediaType = table.at("/definition/mediaType").asText();
                    } else {
                        schemaMediaType = "application/json";
                    }
                    endpoint = new ApiDefinitionEndpointDPDS();
                    endpoint.setName(name);
                    ApiDefinitionEndpointDPDS.Schema schema = new ApiDefinitionEndpointDPDS.Schema();
                    schema.setMediaType(schemaMediaType);
                    schema.setContent(tableSchema);
                    endpoint.setSchema(schema);
                    endpoints.add(endpoint);
                }
            } else if(!apiNode.at("/schema").isMissingNode()) {
                JsonNode rawSchema = apiNode.at("/schema");
                ApiDefinitionEndpointDPDS endpoint;
                String name = null, schemaMediaType = null, realSchema = null;
                if(rawSchema.get("name") != null) {
                    name = rawSchema.get("name").asText();
                } else {
                    name = "endpoint-1";
                }
                realSchema = mapper.writeValueAsString(rawSchema);

                if(!rawSchema.at("/mediaType").isMissingNode()) {
                    schemaMediaType = rawSchema.at("/mediaType").asText();
                } else {
                    schemaMediaType = "application/json";
                }
                endpoint = new ApiDefinitionEndpointDPDS();
                endpoint.setName(name);
                ApiDefinitionEndpointDPDS.Schema schema = new ApiDefinitionEndpointDPDS.Schema();
                schema.setMediaType(schemaMediaType);
                schema.setContent(realSchema);
                endpoint.setSchema(schema);
                endpoints.add(endpoint);
            }
        } catch (JsonProcessingException e) {
            throw new DeserializationException("Impossible to parse api definition", e);
        } catch (URISyntaxException e) {
            throw new DeserializationException("Impossible to parse api definition", e);
        }
        return endpoints;
    }
}
