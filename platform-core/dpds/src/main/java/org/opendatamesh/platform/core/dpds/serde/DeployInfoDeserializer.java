package org.opendatamesh.platform.core.dpds.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.opendatamesh.platform.core.dpds.model.DeployInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ExternalResourceDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;

import java.io.IOException;
import java.util.Map;

public class DeployInfoDeserializer extends StdDeserializer<DeployInfoDPDS> {

    public DeployInfoDeserializer() {
        this(null);
    }

    public DeployInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DeployInfoDPDS deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        
        DeployInfoDPDS infoResource = new DeployInfoDPDS();   

        JsonNode node = jp.getCodec().readTree(jp);

        JsonParser jp2 = null;

        ExternalResourceDPDS serviceRef = null;
        if (node.get("service") != null) {
            jp2 = node.get("service").traverse();
            jp2.nextToken();
            serviceRef = ctxt.readValue(jp2, ExternalResourceDPDS.class);
        }

        StandardDefinitionDPDS templateRef = null;
        if (node.get("template") != null) {
            jp2 = node.get("template").traverse();
            jp2.nextToken();
            templateRef = ctxt.readValue(jp2, StandardDefinitionDPDS.class);
        }
        Map<String, Object> configurationsRef = null;
        if (node.get("configurations") != null) {
            jp2 = node.get("configurations").traverse();
            jp2.nextToken();
            configurationsRef = ctxt.readValue(jp2, Map.class);
        }

        infoResource.setService(serviceRef);
        infoResource.setTemplate(templateRef);
        infoResource.setConfigurations(configurationsRef);
        
        return infoResource;
    }
}