package org.opendatamesh.platform.pp.registry.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ODMRegistryApp {

    public static void main(String[] args) {
        SpringApplication.run(ODMRegistryApp.class, args);
    }

}
