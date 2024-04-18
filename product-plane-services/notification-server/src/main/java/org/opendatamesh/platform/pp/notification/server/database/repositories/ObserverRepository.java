package org.opendatamesh.platform.pp.notification.server.database.repositories;

import org.opendatamesh.platform.pp.notification.server.database.entities.Observer;
import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;

public interface ObserverRepository extends PagingAndSortingAndSpecificationExecutorRepository<Observer, Long> {

    boolean existsByName(String name);

}
