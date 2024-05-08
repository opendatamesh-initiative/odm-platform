package org.opendatamesh.platform.pp.notification.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.notification.server.database.entities.Event;

public interface EventRepository extends PagingAndSortingAndSpecificationExecutorRepository<Event, Long> {

    boolean existsByTypeAndEntityIdAndBeforeStateAndAfterState(
            String type, String entityId, String beforeState, String afterState
    );

}
