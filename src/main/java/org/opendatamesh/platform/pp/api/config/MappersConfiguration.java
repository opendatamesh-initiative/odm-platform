package org.opendatamesh.platform.pp.api.config;

import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.BuildInfoResource;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.DeployInfoResource;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.ProvisionInfoResource;
import org.opendatamesh.platform.pp.api.resources.v1.mappers.BuildInfoResourceDeserializer;
import org.opendatamesh.platform.pp.api.resources.v1.mappers.DeployInfoResourceDeserializer;
import org.opendatamesh.platform.pp.api.resources.v1.mappers.ProvisionInfoResourceDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class MappersConfiguration {
    @Bean
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ProvisionInfoResource.class, new ProvisionInfoResourceDeserializer());
        module.addDeserializer(BuildInfoResource.class, new BuildInfoResourceDeserializer());
        module.addDeserializer(DeployInfoResource.class, new DeployInfoResourceDeserializer());

        objectMapper.registerModule(module);

        return objectMapper;
    }

}
