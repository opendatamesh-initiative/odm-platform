package org.opendatamesh.platform.pp.blueprint.server.configs;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VelocityConfig {

    @Value("${git.templates.path}")
    private String velocityTemplatePath;

    @Value("${git.templates.variable.evaluation.strict}")
    private String velocityStrictEvaluation;

    @Bean
    @Qualifier("apacheVelocityEngine")
    public VelocityEngine velocityEngine() {

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader.file.path", velocityTemplatePath + "/projects");
        velocityEngine.setProperty("runtime.reference.strict", velocityStrictEvaluation);
        velocityEngine.init();

        return velocityEngine;
    }

}
