package org.opendatamesh.platform.core.dpds.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.opendatamesh.platform.core.dpds.model.DeployInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ExternalResourceDPDS;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;

import java.io.IOException;
import java.util.Map;

public class DeployInfoResourceDeserializer extends StdDeserializer<DeployInfoDPDS> {

    public DeployInfoResourceDeserializer() {
        this(null);
    }

    public DeployInfoResourceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DeployInfoDPDS deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        
        DeployInfoDPDS infoResource = null;   

        JsonNode node = jp.getCodec().readTree(jp);

        try {
            JsonParser jp2 = null;

            jp2 = node.get("service").traverse();
            jp2.nextToken();
            ExternalResourceDPDS serviceRef = ctxt.readValue(jp2, ExternalResourceDPDS.class);

            jp2 = node.get("template").traverse();
            jp2.nextToken();
            StandardDefinitionDPDS templateRef = ctxt.readValue(jp2, StandardDefinitionDPDS.class);

            jp2 = node.get("configurations").traverse();
            jp2.nextToken();
            String configurationsRef = ctxt.readValue(jp2, String.class);
            infoResource = new DeployInfoDPDS();

            infoResource.setService(serviceRef);
            infoResource.setTemplate(templateRef);
            infoResource.setConfigurations(configurationsRef);
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n ops");
        }
        return infoResource;
    }
}