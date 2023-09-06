package org.opendatamesh.platform.core.dpds;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;


public class DataProductVersionValidator {

    ObjectMapper mapper;
    
    private JsonSchema dpdsSchema;

    public static final String SCHEMA_URI = "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/schemas/v1.0.0-DRAFT/schema.json";

    public DataProductVersionValidator() {
        mapper = ObjectMapperFactory.JSON_MAPPER;
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V202012);
        
        try {
            dpdsSchema = factory.getSchema(new URI(SCHEMA_URI));
        } catch (URISyntaxException e) {
           throw new RuntimeException("An unexpected exception occured while parsing schema URI [" + SCHEMA_URI + "]");
        }
    }

    public Set<ValidationMessage> validateSchema(JsonNode jsonNode) {
        Set<ValidationMessage> errors = dpdsSchema.validate(jsonNode);   
        return errors;
    }

    public Set<ValidationMessage> validateSchema(String rawContent) throws DeserializationException {
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(rawContent);
        } catch (Throwable t) {
            throw new DeserializationException("Descriptor document is not a valid JSON document", t);
        } 
        return validateSchema(jsonNode);
    }
}
