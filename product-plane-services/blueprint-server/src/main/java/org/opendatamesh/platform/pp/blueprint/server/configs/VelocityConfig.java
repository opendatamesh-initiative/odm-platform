package org.opendatamesh.platform.pp.blueprint.server.configs;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class VelocityConfig {

    @Value("${git.templates.path}")
    private String velocityTemplatePath;

    @Bean
    public VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty("resource.loader.file.path", velocityTemplatePath + "/projects");
        velocityEngine.init(velocityProperties);
        return velocityEngine;
    }

}
