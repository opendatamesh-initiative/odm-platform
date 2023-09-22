package org.opendatamesh.platform.pp.registry.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Data
public class ApplicationStartupConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private Environment env;

    public static String datasourceUrl;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        datasourceUrl = env.getProperty("spring.datasource.url");
    }

}