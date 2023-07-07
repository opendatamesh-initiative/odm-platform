package org.opendatamesh.platform.core.dpds;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.opendatamesh.platform.core.dpds.model.BuildInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.DeployInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ProvisionInfoDPDS;
import org.opendatamesh.platform.core.dpds.serde.BuildInfoResourceDeserializer;
import org.opendatamesh.platform.core.dpds.serde.DeployInfoResourceDeserializer;
import org.opendatamesh.platform.core.dpds.serde.ProvisionInfoResourceDeserializer;

public class ObjectMapperFactory {

    public static ObjectMapper JSON_MAPPER;
    public static ObjectMapper YAML_MAPPER;
    
    static {
        JSON_MAPPER = ObjectMapperFactory.createJson();
        YAML_MAPPER = ObjectMapperFactory.createYaml();
    }

    public static ObjectMapper getRightMapper(String rawContent) {
        if (rawContent.trim().startsWith("{")) {
            return ObjectMapperFactory.JSON_MAPPER;
        }
        return ObjectMapperFactory.YAML_MAPPER;
    }
    
    protected static ObjectMapper createJson() {
        return create(createJsonFactory());
    }

    protected static ObjectMapper createYaml() {
        return create(createYamlFactory());
    }

    private static ObjectMapper create(JsonFactory jsonFactory) {
        ObjectMapper mapper = new ObjectMapper(jsonFactory);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ProvisionInfoDPDS.class, new ProvisionInfoResourceDeserializer());
        module.addDeserializer(BuildInfoDPDS.class, new BuildInfoResourceDeserializer());
        module.addDeserializer(DeployInfoDPDS.class, new DeployInfoResourceDeserializer());
        mapper.registerModule(module);

        return mapper;
    }

    private static JsonFactory createJsonFactory() {
        return new JsonFactoryBuilder()
          .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
          .build();
    }

    private static JsonFactory createYamlFactory() {
        return YAMLFactory.builder()
          .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
          .build();
    }
}
