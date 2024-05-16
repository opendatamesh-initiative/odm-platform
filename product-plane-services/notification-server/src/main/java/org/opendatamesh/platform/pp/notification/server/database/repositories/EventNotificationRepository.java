package org.opendatamesh.platform.pp.notification.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventType;
import org.opendatamesh.platform.pp.notification.server.database.entities.EventNotification;
import org.opendatamesh.platform.pp.notification.server.database.entities.EventNotification_;
import org.opendatamesh.platform.pp.notification.server.database.entities.Event_;
import org.springframework.data.jpa.domain.Specification;

public interface EventNotificationRepository extends PagingAndSortingAndSpecificationExecutorRepository<EventNotification, Long> {

    class Specs extends SpecsUtils {
        public static Specification<EventNotification> hasEventType(EventType eventType) {
            return (
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get(EventNotification_.event).get(Event_.type), eventType)
            );
        }

        public static Specification<EventNotification> hasNotificationStatus(EventNotificationStatus notificationStatus) {
            return (
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get(EventNotification_.status), notificationStatus)
            );
        }
    }

}
