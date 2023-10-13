package org.opendatamesh.platform.pp.blueprint.server.configs;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class VelocityConfig {

    @Bean
    public VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty("file.resource.loader.path", "/tmp");
        velocityEngine.init(velocityProperties);
        return velocityEngine;
    }

}
