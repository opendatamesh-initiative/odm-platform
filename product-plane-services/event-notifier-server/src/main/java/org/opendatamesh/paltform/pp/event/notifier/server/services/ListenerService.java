package org.opendatamesh.paltform.pp.event.notifier.server.services;

import org.opendatamesh.paltform.pp.event.notifier.server.database.entities.Listener;
import org.opendatamesh.paltform.pp.event.notifier.server.database.entities.ListenerSearchOptions;
import org.opendatamesh.paltform.pp.event.notifier.server.database.mappers.ListenerMapper;
import org.opendatamesh.paltform.pp.event.notifier.server.database.repositories.ListenerRepository;
import org.opendatamesh.paltform.pp.event.notifier.server.services.utils.GenericMappedAndFilteredCrudService;
import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.opendatamesh.platform.pp.event.notifier.api.resources.exceptions.EventNotifierApiStandardErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ListenerService extends GenericMappedAndFilteredCrudService<ListenerSearchOptions, ListenerResource, Listener, Long> {

    @Autowired
    private ListenerRepository repository;

    @Autowired
    private ListenerMapper mapper;

    protected ListenerService() {}

    @Override
    protected void validate(Listener objectToValidate) {
        if (objectToValidate == null) {
            throw new BadRequestException(
                    EventNotifierApiStandardErrors.SC400_01_LISTENER_IS_EMPTY,
                    "Listener object cannot be null"
            );
        }

        if (!StringUtils.hasText(objectToValidate.getListenerUrl())) {
            throw new UnprocessableEntityException(
                    EventNotifierApiStandardErrors.SC422_01_LISTENER_IS_INVALID,
                    "Listener adapterUrl cannot be null"
            );
        }
        if (!StringUtils.hasText(objectToValidate.getName())) {
            throw new UnprocessableEntityException(
                    EventNotifierApiStandardErrors.SC422_01_LISTENER_IS_INVALID,
                    "Listener name cannot be null"
            );
        }
    }

    @Override
    protected void beforeCreation(Listener listener) {
        if (repository.existsByName(listener.getName())) {
            throw new UnprocessableEntityException(
                    EventNotifierApiStandardErrors.SC422_02_LISTENER_ALREADY_EXISTS,
                    "Listener with name [" + listener.getName() + "] already exists"
            );
        }
    }

    @Override
    protected void reconcile(Listener objectToReconcile) {
        // No reconcile action needed
    }


    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Listener, Long> getRepository() {
        return repository;
    }

    @Override
    protected Specification<Listener> getSpecFromFilters(ListenerSearchOptions filters) {
        return null;
    }

    @Override
    protected ListenerResource toRes(Listener entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected Listener toEntity(ListenerResource resource) {
        return mapper.toEntity(resource);
    }

}
