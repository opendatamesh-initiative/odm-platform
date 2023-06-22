package org.opendatamesh.platform.pp.registry.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.opendatamesh.platform.core.dpds.model.BuildInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.DeployInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ProvisionInfoDPDS;
import org.opendatamesh.platform.core.dpds.serde.BuildInfoResourceDeserializer;
import org.opendatamesh.platform.core.dpds.serde.DeployInfoResourceDeserializer;
import org.opendatamesh.platform.core.dpds.serde.ProvisionInfoResourceDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfiguration {
    @Bean
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ProvisionInfoDPDS.class, new ProvisionInfoResourceDeserializer());
        module.addDeserializer(BuildInfoDPDS.class, new BuildInfoResourceDeserializer());
        module.addDeserializer(DeployInfoDPDS.class, new DeployInfoResourceDeserializer());

        objectMapper.registerModule(module);

        return objectMapper;
    }

}
