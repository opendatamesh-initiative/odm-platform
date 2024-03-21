package org.opendatamesh.platform.pp.registry.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.opendatamesh.platform.pp.registry.server", "org.opendatamesh.platform.pp.policy.api.services.mappers"})
public class ODMRegistryApp {

    public static void main(String[] args) {
        SpringApplication.run(ODMRegistryApp.class, args);
    }

}
