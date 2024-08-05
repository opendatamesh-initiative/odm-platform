package org.opendatamesh.platform.pp.notification.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.notification.server.database.entities.Observer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ObserverRepository extends PagingAndSortingAndSpecificationExecutorRepository<Observer, Long> {

    Page<Observer> findByName(String name, Pageable pageable);
}
