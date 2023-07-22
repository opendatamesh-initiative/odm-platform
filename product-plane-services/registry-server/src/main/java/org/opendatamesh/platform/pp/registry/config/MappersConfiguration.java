package org.opendatamesh.platform.pp.registry.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.opendatamesh.platform.core.dpds.model.ActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.BuildInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.DeployInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ProvisionInfoDPDS;
import org.opendatamesh.platform.core.dpds.serde.ActivityInfoDeserializer;
import org.opendatamesh.platform.core.dpds.serde.BuildInfoDeserializer;
import org.opendatamesh.platform.core.dpds.serde.DeployInfoDeserializer;
import org.opendatamesh.platform.core.dpds.serde.LifecycleInfoDeserializer;
import org.opendatamesh.platform.core.dpds.serde.ProvisionInfoDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfiguration {
    @Bean
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ActivityInfoDPDS.class, new ActivityInfoDeserializer());
        module.addDeserializer(LifecycleInfoDPDS.class, new LifecycleInfoDeserializer());
        module.addDeserializer(ProvisionInfoDPDS.class, new ProvisionInfoDeserializer());
        module.addDeserializer(BuildInfoDPDS.class, new BuildInfoDeserializer());
        module.addDeserializer(DeployInfoDPDS.class, new DeployInfoDeserializer());

        objectMapper.registerModule(module);

        return objectMapper;
    }

}
