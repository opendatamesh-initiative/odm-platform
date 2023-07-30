package org.opendatamesh.platform.core.dpds.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.BuildInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ExternalResourceDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

public class LifecycleInfoDeserializer extends StdDeserializer<LifecycleInfoDPDS> {

    public LifecycleInfoDeserializer() {
        this(null);
    }

    public LifecycleInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public LifecycleInfoDPDS deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        LifecycleInfoDPDS lifecycleInfo = new LifecycleInfoDPDS();

        JsonNode node = jp.getCodec().readTree(jp);

        Iterator<Entry<String, JsonNode>> stages = node.fields();

        JsonParser jp2 = null;
        
        while (stages.hasNext()) {
            Entry<String, JsonNode> stage = stages.next();
            String stageName = stage.getKey();
            jp2 = node.get(stageName).traverse();
            jp2.setCodec(jp.getCodec());
            jp2.nextToken();
            LifecycleActivityInfoDPDS activityInfo = null;
            activityInfo = ctxt.readValue(jp2, LifecycleActivityInfoDPDS.class);
            activityInfo.setStageName(stageName);
            lifecycleInfo.getActivityInfos().add(activityInfo);
        }

        return lifecycleInfo;
    }
}