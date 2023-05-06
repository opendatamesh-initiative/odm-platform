package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import java.io.IOException;
import java.util.Map;

import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ExternalResourceResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ProvisionInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ReferenceObjectResource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ProvisionInfoResourceDeserializer extends StdDeserializer<ProvisionInfoResource> {

    public ProvisionInfoResourceDeserializer() {
        this(null);
    }

    public ProvisionInfoResourceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ProvisionInfoResource deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        
        ProvisionInfoResource infoResource = null;        
        JsonNode node = jp.getCodec().readTree(jp);
        //String service = node.get("service").asText();

        try {
            JsonParser jp2 = null;

            jp2 = node.get("service").traverse();
            jp2.nextToken();
            ExternalResourceResource serviceRef = ctxt.readValue(jp2, ExternalResourceResource.class);

            jp2 = node.get("template").traverse();
            jp2.nextToken();
            ExternalResourceResource templateRef = ctxt.readValue(jp2, ExternalResourceResource.class);

            jp2 = node.get("configurations").traverse();
            jp2.nextToken();
            Map<String, Object> configurationsRef = ctxt.readValue(jp2, Map.class);
            
            infoResource = new ProvisionInfoResource();
            infoResource.setService(serviceRef);
            infoResource.setTemplate(templateRef);
            infoResource.setConfigurations(configurationsRef);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return infoResource;
    }
}