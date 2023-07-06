package org.opendatamesh.platform.core.dpds.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.opendatamesh.platform.core.dpds.model.BuildInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;

import java.io.IOException;
import java.util.Map;

public class BuildInfoResourceDeserializer extends StdDeserializer<BuildInfoDPDS> {

    public BuildInfoResourceDeserializer() {
        this(null);
    }

    public BuildInfoResourceDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BuildInfoDPDS deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        BuildInfoDPDS infoResource = null;   

        JsonNode node = jp.getCodec().readTree(jp);

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

            infoResource = new BuildInfoDPDS();
            infoResource.setService(serviceRef);
            infoResource.setTemplate(templateRef);
            infoResource.setConfigurations(configurationsRef);
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n ops");
        }
        return infoResource;
    }
}