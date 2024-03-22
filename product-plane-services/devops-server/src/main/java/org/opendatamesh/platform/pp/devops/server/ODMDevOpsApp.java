package org.opendatamesh.platform.pp.devops.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ODMDevOpsApp {

    public static void main(String[] args) {
        SpringApplication.run(ODMDevOpsApp.class, args);
    }

}
