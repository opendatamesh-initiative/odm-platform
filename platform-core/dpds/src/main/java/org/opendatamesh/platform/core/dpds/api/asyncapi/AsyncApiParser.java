package org.opendatamesh.platform.core.dpds.api.asyncapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opendatamesh.platform.core.dpds.UriFetcher;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionEndpointDPDS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AsyncApiParser {
    URI baseUri;
    
    public AsyncApiParser(URI baseUri) {
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
            if(!apiNode.has("asyncapi")) return null;
            if(apiNode.get("channels") != null){
                ObjectNode channels = (ObjectNode)apiNode.get("channels");
                Iterator<String> channelNames = channels.fieldNames();
                int i = 0;
                while (channelNames.hasNext()) {
                    ObjectNode channel = (ObjectNode)channels.get(channelNames.next());
                    ObjectNode subscribeOperation =  (ObjectNode)channel.get("subscribe");
                    if(subscribeOperation == null) continue;
                    ObjectNode message =  (ObjectNode)subscribeOperation.get("message");
                    if(message == null) continue;
                    JsonNode payload =  (ObjectNode)message.get("payload");
                    if(payload == null) continue;
                    
                    
                    ApiDefinitionEndpointDPDS endpoint;
                    String name = null, mediaType = null, operationSchema = null;
                   
                    if(subscribeOperation.get("operationId") != null) {
                        name = subscribeOperation.get("operationId").asText();
                    } else {
                        name = "endpoint-" + (i+1);
                    }
                    
                    if(payload.get("$ref") != null) {
                        UriFetcher fetcher = new UriFetcher(baseUri);
                        operationSchema = fetcher.fetch(new URI(payload.get("$ref").asText()));
                    } else {
                        operationSchema = payload.toPrettyString();
                    }
                    
                    

                    if(message.get("contentType") != null) {
                        mediaType = message.get("contentType").asText();
                    } else {
                        mediaType = "application/json";
                    }
                    endpoint = new ApiDefinitionEndpointDPDS();
                    endpoint.setName(name);
                    endpoint.setMediaType(mediaType);
                    endpoint.setSchema(operationSchema);
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
