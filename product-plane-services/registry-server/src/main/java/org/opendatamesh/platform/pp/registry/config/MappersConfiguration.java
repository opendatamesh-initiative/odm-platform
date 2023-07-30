package org.opendatamesh.platform.pp.registry.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;

import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.serde.ActivityInfoDeserializer;

import org.opendatamesh.platform.core.dpds.serde.LifecycleInfoDeserializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfiguration {
    @Bean
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(LifecycleActivityInfoDPDS.class, new ActivityInfoDeserializer());
        module.addDeserializer(LifecycleInfoDPDS.class, new LifecycleInfoDeserializer());
       

        objectMapper.registerModule(module);

        return objectMapper;
    }

}
