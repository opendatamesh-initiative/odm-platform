package org.opendatamesh.platform.pp.notification.server.controllers;

import org.opendatamesh.platform.pp.notification.api.controllers.AbstractObserverController;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverSearchOptions;
import org.opendatamesh.platform.pp.notification.server.services.ObserverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ObserverControllerImpl extends AbstractObserverController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ObserverService observerService;

    @Override
    public ObserverResource addObserver(ObserverResource observerResource) {
        ObserverResource result = observerService.createResource(observerResource);
        log.info("Registered new observer: {} , URL: {} , ID: {}", result.getName(), result.getObserverServerBaseUrl(), result.getId());
        return result;
    }

    @Override
    public ObserverResource updateObserver(Long listenerId, ObserverResource listenerResource) {
        ObserverResource result = observerService.overwriteResource(listenerId, listenerResource);
        log.info("Updated observer: {} , URL: {}, ID: {}", result.getName(), result.getObserverServerBaseUrl(), result.getId());
        return result;
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