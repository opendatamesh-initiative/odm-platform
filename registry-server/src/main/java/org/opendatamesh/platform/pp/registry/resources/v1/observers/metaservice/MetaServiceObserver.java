package org.opendatamesh.platform.pp.registry.resources.v1.observers.metaservice;

import lombok.Data;
import org.opendatamesh.notification.EventResource;
import org.opendatamesh.platform.pp.registry.resources.v1.observers.Observer;
import org.opendatamesh.platform.pp.registry.services.MetaServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class MetaServiceObserver implements Observer {

    @Autowired
    private MetaServiceProxy metaServiceProxy;

    @Override
    public void notify(EventResource event) {
        metaServiceProxy.postEventToMetaService(event);
    }
}
