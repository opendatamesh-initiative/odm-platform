package org.opendatamesh.platform.pp.notification.server.services;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.NotificationApiStandardErrors;
import org.opendatamesh.platform.pp.notification.server.database.entities.EventNotification;
import org.opendatamesh.platform.pp.notification.server.database.mappers.EventNotificationMapper;
import org.opendatamesh.platform.pp.notification.server.database.repositories.EventNotificationRepository;
import org.opendatamesh.platform.pp.notification.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventNotificationService extends GenericMappedAndFilteredCrudService<EventNotificationSearchOptions, EventNotificationResource, EventNotification, Long> {

    @Autowired
    private EventNotificationRepository repository;

    @Autowired
    private EventNotificationMapper mapper;

    protected EventNotificationService() {}

    @Override
    protected void validate(EventNotification objectToValidate) {
        if (objectToValidate == null) {
            throw new BadRequestException(
                    NotificationApiStandardErrors.SC400_03_NOTIFICATION_IS_EMTPY,
                    "Notification object cannot be null"
            );
        }
        if (objectToValidate.getEvent() == null || objectToValidate.getEventId() == null) {
            throw new UnprocessableEntityException(
                    NotificationApiStandardErrors.SC422_04_NOTIFICATION_IS_INVALID,
                    "Notification Event object cannot be null"
            );
        }
        if (objectToValidate.getObserver() == null || objectToValidate.getObserverId() == null) {
            throw new UnprocessableEntityException(
                    NotificationApiStandardErrors.SC422_04_NOTIFICATION_IS_INVALID,
                    "Notification Observer object cannot be null"
            );
        }
        if (objectToValidate.getStatus() == null) {
            throw new UnprocessableEntityException(
                    NotificationApiStandardErrors.SC422_04_NOTIFICATION_IS_INVALID,
                    "Notification Status cannot be null"
            );
        }
    }

    @Override
    protected void reconcile(EventNotification objectToReconcile) {
        // No reconcile action needed
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<EventNotification, Long> getRepository() {
        return repository;
    }

    @Override
    protected Specification<EventNotification> getSpecFromFilters(EventNotificationSearchOptions filters) {
        List<Specification<EventNotification>> specifications = new ArrayList<>();
        if (filters.getEventType() != null) {
            specifications.add(EventNotificationRepository.Specs.hasEventType(filters.getEventType()));
        }
        if (filters.getNotificationStatus() != null) {
            specifications.add(EventNotificationRepository.Specs.hasNotificationStatus(filters.getNotificationStatus()));
        }
        return SpecsUtils.combineWithAnd(specifications);
    }

    @Override
    protected EventNotificationResource toRes(EventNotification entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected EventNotification toEntity(EventNotificationResource resource) {
        return mapper.toEntity(resource);
    }

    @Override
    protected Class<EventNotification> getEntityClass() {
        return EventNotification.class;
    }
    
}
