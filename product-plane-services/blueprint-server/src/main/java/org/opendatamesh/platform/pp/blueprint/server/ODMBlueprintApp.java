package org.opendatamesh.platform.pp.blueprint.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan({"org.opendatamesh.platform.pp.blueprint.server"})
public class ODMBlueprintApp {

    public static void main(String[] args) {
        SpringApplication.run(ODMBlueprintApp.class, args);
    }

}