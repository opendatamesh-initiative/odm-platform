package org.opendatamesh.platform.pp.notification.server.services;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventSearchOptions;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.NotificationApiStandardErrors;
import org.opendatamesh.platform.pp.notification.server.database.entities.Event;
import org.opendatamesh.platform.pp.notification.server.database.mappers.EventMapper;
import org.opendatamesh.platform.pp.notification.server.database.repositories.EventRepository;
import org.opendatamesh.platform.pp.notification.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EventService extends GenericMappedAndFilteredCrudService<EventSearchOptions, EventResource, Event, Long> {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Override
    protected void validate(Event objectToValidate) {
        if (objectToValidate == null) {
            throw new BadRequestException(
                    NotificationApiStandardErrors.SC400_02_EVENT_IS_EMPTY,
                    "Event object cannot be null"
            );
        }
        if (objectToValidate.getType() == null) {
            throw new BadRequestException(
                    NotificationApiStandardErrors.SC422_03_EVENT_IS_INVALID,
                    "Event type cannot be null"
            );
        }
        if (objectToValidate.getAfterState() == null && objectToValidate.getBeforeState() == null) {
            throw new BadRequestException(
                    NotificationApiStandardErrors.SC422_03_EVENT_IS_INVALID,
                    "Event afterState and beforeState cannot be both null"
            );
        }
    }

    @Override
    protected void reconcile(Event objectToReconcile) {
        // No reconcile action needed
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Event, Long> getRepository() {
        return eventRepository;
    }

    @Override
    protected Specification<Event> getSpecFromFilters(EventSearchOptions filters) {
        return null;
    }

    @Override
    protected EventResource toRes(Event entity) {
        return eventMapper.toRes(entity);
    }

    @Override
    protected Event toEntity(EventResource resource) {
        return eventMapper.toEntity(resource);
    }

    @Override
    protected Class<Event> getEntityClass() {
        return Event.class;
    }

}