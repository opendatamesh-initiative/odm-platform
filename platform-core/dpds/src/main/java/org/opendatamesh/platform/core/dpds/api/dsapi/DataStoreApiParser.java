package org.opendatamesh.platform.core.dpds.api.dsapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.opendatamesh.platform.core.dpds.UriFetcher;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionEndpointDPDS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

@Data
public class DataStoreApiParser {

    URI baseUri;
    
    public DataStoreApiParser(URI baseUri) {
        this.baseUri = baseUri;
    }

    public ApiDefinitionDPDS parse(String rawContent) throws ParseException, FetchException {
        ApiDefinitionDPDS api = new ApiDefinitionDPDS();
        api.setBaseUri(baseUri);
        api.setRawContent(rawContent);
        api.setEndpoints( extractEndpoints(rawContent) );
        return api;
    }

    private List<ApiDefinitionEndpointDPDS> extractEndpoints(String rawContent) throws ParseException, FetchException {
        List<ApiDefinitionEndpointDPDS> endpoints = new ArrayList<ApiDefinitionEndpointDPDS>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode apiNode = (ObjectNode)mapper.readTree(rawContent);
            if(!apiNode.has("datastoreapi")) return null;
            if(!apiNode.at("/schema/tables").isMissingNode()){
                ArrayNode tables = (ArrayNode)apiNode.at("/schema/tables");
                for(int i = 0; i < tables.size(); i++) {
                    ApiDefinitionEndpointDPDS endpoint;
                    String name = null, mediaType = null, tableSchema = null;
                    ObjectNode table = (ObjectNode)tables.get(i);
                    if(table.get("name") != null) {
                        name = table.get("name").asText();
                    } else {
                        name = "endpoint-" + (i+1);
                    }
                    if(!table.at("/definition/$ref").isMissingNode()) {
                        UriFetcher fetcher = new UriFetcher(baseUri);
                        tableSchema = fetcher.fetch(new URI(table.at("/definition/$ref").asText()));
                    } else {
                        tableSchema = mapper.writeValueAsString(table.at("/definition"));
                    }

                    if(!table.at("/definition/mediaType").isMissingNode()) {
                        mediaType = table.at("/definition/mediaType").asText();
                    } else {
                        mediaType = "application/json";
                    }
                    endpoint = new ApiDefinitionEndpointDPDS();
                    endpoint.setName(name);
                    endpoint.setMediaType(mediaType);
                    endpoint.setSchema(tableSchema);
                    endpoints.add(endpoint);
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
