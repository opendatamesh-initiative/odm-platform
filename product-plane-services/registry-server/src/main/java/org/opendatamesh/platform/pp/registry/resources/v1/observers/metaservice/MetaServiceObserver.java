package org.opendatamesh.platform.pp.registry.resources.v1.observers.metaservice;

import org.opendatamesh.notification.EventResource;
import org.opendatamesh.platform.pp.registry.resources.v1.observers.Observer;
import org.opendatamesh.platform.pp.registry.services.MetaServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetaServiceObserver implements Observer {

    @Autowired
    private MetaServiceProxy metaServiceProxy;

    public MetaServiceObserver() {
        this.metaServiceProxy = new MetaServiceProxy();
    }

    @Override
    public void notify(EventResource event) {
        metaServiceProxy.postEventToMetaService(event);
    }
}
