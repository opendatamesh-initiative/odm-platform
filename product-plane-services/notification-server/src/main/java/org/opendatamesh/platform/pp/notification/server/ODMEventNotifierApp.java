package org.opendatamesh.platform.pp.notification.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ODMEventNotifierApp {

    public static void main(String[] args) {
        SpringApplication.run(ODMEventNotifierApp.class, args);
    }

}