package org.opendatamesh.platform.pp.api.resources.v1.mappers;

import java.io.IOException;
import java.util.Map;

import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.DeployInfoResource;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.ExternalResourceResource;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.ReferenceObjectResource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class DeployInfoResourceDeserializer extends StdDeserializer<DeployInfoResource> {

    public DeployInfoResourceDeserializer() {
        this(null);
    }

    public DeployInfoResourceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DeployInfoResource deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        
        DeployInfoResource infoResource = null;   

        JsonNode node = jp.getCodec().readTree(jp);

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
            infoResource = new DeployInfoResource();

            infoResource.setService(serviceRef);
            infoResource.setTemplate(templateRef);
            infoResource.setConfigurations(configurationsRef);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return infoResource;
    }
}