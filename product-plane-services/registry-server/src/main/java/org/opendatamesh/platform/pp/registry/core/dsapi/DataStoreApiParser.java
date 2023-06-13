package org.opendatamesh.platform.pp.registry.core.dsapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.opendatamesh.platform.pp.registry.core.UriFetcher;
import org.opendatamesh.platform.pp.registry.core.exceptions.FetchException;
import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.resources.v1.shared.DataServiceApiEndpointResource;
import org.opendatamesh.platform.pp.registry.resources.v1.shared.ApiResource;

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

    public ApiResource parse(String rawContent) throws ParseException, FetchException {
        ApiResource api = new ApiResource();
        api.setBaseUri(baseUri);
        api.setRawContent(rawContent);
        api.setEndpoints( extractEndpoints(rawContent) );
        return api;
    }

    private List<DataServiceApiEndpointResource> extractEndpoints(String rawContent) throws ParseException, FetchException {
        List<DataServiceApiEndpointResource> endpoints = new ArrayList<DataServiceApiEndpointResource>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode apiNode = (ObjectNode)mapper.readTree(rawContent);
            if(!apiNode.has("datastoreapi")) return null;
            if(!apiNode.at("/schema/tables").isMissingNode()){
                ArrayNode tables = (ArrayNode)apiNode.at("/schema/tables");
                for(int i = 0; i < tables.size(); i++) {
                    DataServiceApiEndpointResource endpoint;
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
                    endpoint = new DataServiceApiEndpointResource();
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
