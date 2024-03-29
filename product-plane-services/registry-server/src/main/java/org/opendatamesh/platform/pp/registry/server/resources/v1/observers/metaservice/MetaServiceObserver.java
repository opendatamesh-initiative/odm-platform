package org.opendatamesh.platform.pp.registry.server.resources.v1.observers.metaservice;


import org.opendatamesh.platform.pp.registry.server.resources.v1.observers.Observer;
import org.opendatamesh.platform.pp.registry.server.services.MetaServiceProxy;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetaServiceObserver implements Observer {

    @Autowired
    private MetaServiceProxy metaServiceProxy;

    public MetaServiceObserver() {
    }

    @Override
    public void notify(EventResource event) {
        metaServiceProxy.postEventToMetaService(event);
    }
}
