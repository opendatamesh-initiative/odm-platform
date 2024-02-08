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

    @Value("${git.templates.variable.delimiter.start}")
    private String velocityVariableDelimiterStart;

    @Value("${git.templates.variable.delimiter.stop}")
    private String velocityVariableDelimiterStop;

    @Value("${git.templates.variable.evaluation.strict}")
    private String velocityStrictEvaluation;

    @Bean
    public VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty("resource.loader.file.path", velocityTemplatePath + "/projects");
        velocityProperties.setProperty("runtime.interpolate.string", velocityStrictEvaluation);
        velocityProperties.setProperty("runtime.interpolate.stopstring", velocityStrictEvaluation);
        velocityProperties.setProperty("runtime.reference.strict", velocityStrictEvaluation);
        velocityEngine.init(velocityProperties);
        return velocityEngine;
    }

}
