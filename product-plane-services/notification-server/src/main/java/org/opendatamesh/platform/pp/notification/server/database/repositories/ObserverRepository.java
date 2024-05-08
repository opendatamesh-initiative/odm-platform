package org.opendatamesh.platform.pp.notification.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.notification.server.database.entities.Observer;

public interface ObserverRepository extends PagingAndSortingAndSpecificationExecutorRepository<Observer, Long> {

    boolean existsByName(String name);

}
