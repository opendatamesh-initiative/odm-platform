package org.opendatamesh.platform.pp.devops.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class ODMDevOpsApp {

    public static void main(String[] args) {
        SpringApplication.run(ODMDevOpsApp.class, args);
    }

}
