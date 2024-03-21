package org.opendatamesh.platform.pp.devops.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.opendatamesh.platform.pp.devops.server", "org.opendatamesh.platform.pp.policy.api.services.mappers"})
public class ODMDevOpsApp {

    public static void main(String[] args) {
        SpringApplication.run(ODMDevOpsApp.class, args);
    }

}
