package org.opendatamesh.platform.pp.notification.server.services;

import org.opendatamesh.platform.pp.notification.server.database.entities.Observer;
import org.opendatamesh.platform.pp.notification.server.database.mappers.ObserverMapper;
import org.opendatamesh.platform.pp.notification.server.database.repositories.ObserverRepository;
import org.opendatamesh.platform.pp.notification.server.services.utils.GenericMappedAndFilteredCrudService;
import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverSearchOptions;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.NotificationApiStandardErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ObserverService extends GenericMappedAndFilteredCrudService<ObserverSearchOptions, ObserverResource, Observer, Long> {

    @Autowired
    private ObserverRepository repository;

    @Autowired
    private ObserverMapper mapper;

    protected ObserverService() {}

    @Override
    protected void validate(Observer objectToValidate) {
        if (objectToValidate == null) {
            throw new BadRequestException(
                    NotificationApiStandardErrors.SC400_01_OBSERVER_IS_EMPTY,
                    "Observer object cannot be null"
            );
        }
        if (!StringUtils.hasText(objectToValidate.getObserverServerBaseUrl())) {
            throw new UnprocessableEntityException(
                    NotificationApiStandardErrors.SC422_01_OBSERVER_IS_INVALID,
                    "Observer server base URL cannot be null"
            );
        }
        if (!StringUtils.hasText(objectToValidate.getName())) {
            throw new UnprocessableEntityException(
                    NotificationApiStandardErrors.SC422_01_OBSERVER_IS_INVALID,
                    "Observer name cannot be null"
            );
        }
    }

    @Override
    protected void beforeCreation(Observer observer) {
        if (repository.existsByName(observer.getName())) {
            throw new UnprocessableEntityException(
                    NotificationApiStandardErrors.SC422_02_OBSERVER_ALREADY_EXISTS,
                    "Observer with name [" + observer.getName() + "] already exists"
            );
        }
    }

    @Override
    protected void reconcile(Observer objectToReconcile) {
        // No reconcile action needed
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Observer, Long> getRepository() {
        return repository;
    }

    @Override
    protected Specification<Observer> getSpecFromFilters(ObserverSearchOptions filters) {
        return null;
    }

    @Override
    protected ObserverResource toRes(Observer entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected Observer toEntity(ObserverResource resource) {
        return mapper.toEntity(resource);
    }

    @Override
    protected Class<Observer> getEntityClass() {
        return Observer.class;
    }

}
