package org.opendatamesh.platform.core.dpds.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.opendatamesh.platform.core.dpds.model.core.ExternalResourceDPDS;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleTaskInfoDPDS;

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

        JsonNode lifecycleInfoNode = jp.getCodec().readTree(jp);

        Iterator<Entry<String, JsonNode>> stages = lifecycleInfoNode.fields();

        JsonParser jp2 = null;
        
        while (stages.hasNext()) {
            Entry<String, JsonNode> stage = stages.next();
            String stageName = stage.getKey();

            if(lifecycleInfoNode.get(stageName).isArray()) {
                ArrayNode taskNodes = (ArrayNode)lifecycleInfoNode.get(stageName);
                for(int i = 0; i < taskNodes.size(); i++) {
                    jp2 = taskNodes.get(i).traverse();
                    jp2.setCodec(jp.getCodec());
                    jp2.nextToken();
                    LifecycleTaskInfoDPDS taskInfo = null;
                    taskInfo = ctxt.readValue(jp2, LifecycleTaskInfoDPDS.class);
                    taskInfo.setStageName(stageName);
                    if(taskInfo.getOrder() == null) taskInfo.setOrder(i+1);
                    if(taskInfo.getName() == null) taskInfo.setName(stageName + ":task:" +  taskInfo.getOrder());
                    lifecycleInfo.getTasksInfo().add(taskInfo);
                }
            } else {
                jp2 = lifecycleInfoNode.get(stageName).traverse();
                throw new IOException("Expected an array as value of property [" + stageName + "] at " + jp2.getTokenLocation().toString());
            }
            /* 
            else if(lifecycleInfoNode.get(stageName).isObject()) {
                jp2 = node.get(stageName).traverse();
                jp2.setCodec(jp.getCodec());
                jp2.nextToken();
                LifecycleTaskInfoDPDS taskInfo = null;
                taskInfo = ctxt.readValue(jp2, LifecycleTaskInfoDPDS.class);
                taskInfo.setStageName(stageName);
                lifecycleInfo.getTasksInfo().add(taskInfo);
                
            }
            */
            
            
          
        }

        return lifecycleInfo;
    }
}