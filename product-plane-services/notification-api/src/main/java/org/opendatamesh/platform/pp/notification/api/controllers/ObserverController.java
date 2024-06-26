package org.opendatamesh.platform.pp.notification.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ObserverController {

    ObserverResource addObserver(ObserverResource observerResource);

    ObserverResource updateObserver(Long id, ObserverResource observerResource);

    Page<ObserverResource> getObservers(Pageable pageable, ObserverSearchOptions searchOptions);

    ObserverResource getObserver(Long id);

    void removeObserver(Long id);

}
