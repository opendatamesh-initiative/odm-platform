package org.opendatamesh.platform.pp.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OpenDataMeshApp {
    public static void main(String[] args) {
        SpringApplication.run(OpenDataMeshApp.class, args);
    }

}
