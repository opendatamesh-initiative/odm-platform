package org.opendatamesh.platform.core.dpds.serde;

import java.io.IOException;
import java.util.Map;

import org.opendatamesh.platform.core.dpds.model.ProvisionInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ProvisionInfoResourceDeserializer extends StdDeserializer<ProvisionInfoDPDS> {

    public ProvisionInfoResourceDeserializer() {
        this(null);
    }

    public ProvisionInfoResourceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ProvisionInfoDPDS deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        
        ProvisionInfoDPDS infoResource = null;        
        JsonNode node = jp.getCodec().readTree(jp);
        //String service = node.get("service").asText();

        try {
            JsonParser jp2 = null;

            jp2 = node.get("service").traverse();
            jp2.nextToken();
            ReferenceObjectDPDS serviceRef = ctxt.readValue(jp2, ReferenceObjectDPDS.class);

            jp2 = node.get("template").traverse();
            jp2.nextToken();
            ReferenceObjectDPDS templateRef = ctxt.readValue(jp2, ReferenceObjectDPDS.class);

            jp2 = node.get("configurations").traverse();
            jp2.nextToken();
            Map<String, Object> configurationsRef = ctxt.readValue(jp2, Map.class);
            
            infoResource = new ProvisionInfoDPDS();
            infoResource.setService(serviceRef);
            infoResource.setTemplate(templateRef);
            infoResource.setConfigurations(configurationsRef);
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n ops");
        }
        return infoResource;
    }
}