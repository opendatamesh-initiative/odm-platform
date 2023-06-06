package org.opendatamesh.platform.pp.registry.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;

@Component
public class DataProductDescriptorValidator {

    @Autowired
    ObjectMapper mapper;
    
    private JsonSchema dpdsSchema;

    public static final String SCHEMA_URI = "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/schemas/v1.0.0-DRAFT/schema.json";

    public DataProductDescriptorValidator() {
        mapper = new ObjectMapper();
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

    public Set<ValidationMessage> validateSchema(String rawContent) throws ParseException {
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(rawContent);
        } catch (Throwable t) {
            throw new ParseException("Descriptor document it's not a valid JSON document", t);
        } 
        return validateSchema(jsonNode);
    }
}
