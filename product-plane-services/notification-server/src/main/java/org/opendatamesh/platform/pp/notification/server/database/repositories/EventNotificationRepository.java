package org.opendatamesh.platform.pp.notification.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.pp.notification.server.database.entities.EventNotification;
import org.opendatamesh.platform.pp.notification.server.database.entities.EventNotification_;
import org.springframework.data.jpa.domain.Specification;

public interface EventNotificationRepository extends PagingAndSortingAndSpecificationExecutorRepository<EventNotification, Long> {

    class Specs extends SpecsUtils {
        public static Specification<EventNotification> hasEventType(String eventType) {
            return (
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get(EventNotification_.event.getType().toString()), eventType)
            );
        }

        public static Specification<EventNotification> hasNotificationStatus(String notificationStatus) {
            return (
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get(EventNotification_.status), notificationStatus)
            );
        }
    }

}
