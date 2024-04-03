package org.opendatamesh.paltform.pp.event.notifier.server.database.repositories;

import org.opendatamesh.paltform.pp.event.notifier.server.database.entities.Listener;
import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;

public interface ListenerRepository extends PagingAndSortingAndSpecificationExecutorRepository<Listener, Long> {

    boolean existsByName(String name);

}
