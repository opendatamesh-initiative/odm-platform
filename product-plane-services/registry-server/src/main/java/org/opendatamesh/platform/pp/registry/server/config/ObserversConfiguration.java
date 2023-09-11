package org.opendatamesh.platform.pp.registry.server.config;

import org.opendatamesh.platform.pp.registry.server.resources.v1.observers.EventNotifier;
import org.opendatamesh.platform.pp.registry.server.resources.v1.observers.metaservice.MetaServiceObserver;
import org.opendatamesh.platform.pp.registry.server.services.MetaServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserversConfiguration {

    @Value("${odm.utilityPlane.metaService.active}")
    private Boolean metaServiceActive;

    @Autowired
    MetaServiceObserver metaServiceObserver;

    private static final Logger logger = LoggerFactory.getLogger(MetaServiceProxy.class);

    @Bean
    public EventNotifier myEventNotifier() {

        // Create EventNotifier
        EventNotifier eventNotifier = new EventNotifier();

        // Add observers
        if(!metaServiceActive) {
            logger.debug("Skipping meta service");
        } else {
            eventNotifier.addObserver(metaServiceObserver);
        }

        return eventNotifier;
    }

}
