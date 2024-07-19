package org.opendatamesh.platform.pp.params.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan({"org.opendatamesh.platform.pp.params.server"})
public class ODMParamsApp {
    public static void main(String[] args) {
        SpringApplication.run(ODMParamsApp.class, args);
    }

}