package org.opendatamesh.platform.pp.registry.core.dsapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.opendatamesh.platform.pp.registry.core.UriFetcher;
import org.opendatamesh.platform.pp.registry.core.exceptions.FetchException;
import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

@Data
public class DataStoreApi {

    URI baseUri;
    String rawContent;

    public DataStoreApi(URI baseUri, String rawContent) {
        this.baseUri = baseUri;
        this.rawContent = rawContent;
    }

    public List<String> getTableSchemas() throws ParseException, FetchException {
        List<String> schemas = new ArrayList<String>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode apiNode = (ObjectNode)mapper.readTree(rawContent);
            if(!apiNode.has("datastoreapi")) return null;
            if(!apiNode.at("/schema/tables").isMissingNode()){
                ArrayNode tables = (ArrayNode)apiNode.at("/schema/tables");
                for(int i = 0; i < tables.size(); i++) {
                    String tableSchema = null;
                    ObjectNode table = (ObjectNode)tables.get(i);
                    if(!table.at("/definition/$ref").isMissingNode()) {
                        UriFetcher fetcher = new UriFetcher(baseUri);
                        tableSchema = fetcher.fetch(new URI(table.at("/definition/$ref").asText()));
                    } else {
                        tableSchema = mapper.writeValueAsString(table.at("/definition"));
                    }
                    schemas.add(tableSchema);
                }
            }
        } catch (JsonProcessingException e) {
            throw new ParseException("Impossible to parse api definition", e);
        } catch (URISyntaxException e) {
            throw new ParseException("Impossible to parse api definition", e);
        }
        return schemas;
    }
}
