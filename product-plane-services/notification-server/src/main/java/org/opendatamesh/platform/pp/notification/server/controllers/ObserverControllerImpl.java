package org.opendatamesh.platform.pp.notification.server.controllers;

import org.opendatamesh.platform.pp.notification.api.controllers.AbstractObserverController;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverSearchOptions;
import org.opendatamesh.platform.pp.notification.server.services.ObserverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ObserverControllerImpl extends AbstractObserverController {

    @Autowired
    ObserverService observerService;

    @Override
    public ObserverResource addObserver(ObserverResource observerResource) {
        return observerService.createResource(observerResource);
    }

    @Override
    public ObserverResource updateObserver(Long listenerId, ObserverResource listenerResource) {
        return observerService.overwriteResource(listenerId, listenerResource);
    }

    @Override
    public Page<ObserverResource> getObservers(Pageable pageable, ObserverSearchOptions searchOptions) {
        return observerService.findAllResourcesFiltered(pageable, searchOptions);
    }

    @Override
    public ObserverResource getObserver(Long id) {
        return observerService.findOneResource(id);
    }

    @Override
    public void removeObserver(Long id) {
        observerService.delete(id);
    }

}