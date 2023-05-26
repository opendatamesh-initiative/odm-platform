package org.opendatamesh.platform.pp.registry.config;

import org.opendatamesh.platform.pp.registry.resources.v1.observers.EventNotifier;
import org.opendatamesh.platform.pp.registry.resources.v1.observers.metaservice.MetaServiceObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserversConfiguration {

    @Autowired
    MetaServiceObserver metaServiceObserver;

    @Bean
    public EventNotifier myEventNotifier() {

        // Create EventNotifier
        EventNotifier eventNotifier = new EventNotifier();

        // Add observers
        eventNotifier.addObserver(metaServiceObserver);

        return eventNotifier;
    }

}
