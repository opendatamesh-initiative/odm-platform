package org.opendatamesh.platform.pp.policy.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan({"org.opendatamesh.platform.pp.policy.server"})
public class ODMPolicyApp {
    public static void main(String[] args) {
        SpringApplication.run(ODMPolicyApp.class, args);
    }
}
