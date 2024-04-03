package org.opendatamesh.paltform.pp.event.notifier.server.services.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

public abstract class GenericMappedCrudService<R, T, ID extends Serializable> extends GenericCrudService<T, ID> {

    @Autowired
    private TransactionHandler transactionHandler;

    protected abstract R toRes(T entity);

    protected abstract T toEntity(R resource);

    public final Page<R> findAllResources(Pageable pageable) {
        return transactionHandler.runInTransaction(() -> {
            final Page<T> entitiesPage = findAll(pageable);
            return entitiesPage.map(this::toRes);
        });
    }

    public final R findOneResource(ID identifier) {
        return transactionHandler.runInTransaction(() -> {
            final T entity = findOne(identifier);
            return toRes(entity);
        });
    }

    public final R createResource(R objectToCreate) {
        T entityToCreate = toEntity(objectToCreate);
        T createdEntity = create(entityToCreate);
        return toRes(createdEntity);
    }

    public final R overwriteResource(ID identifier, R objectToOverwrite) {
        T entityToOverwrite = toEntity(objectToOverwrite);
        T overwrittenEntity = overwrite(identifier, entityToOverwrite);
        return toRes(overwrittenEntity);
    }

    public final R deleteReturningResource(ID identifier) {
        return deleteReturning(identifier, this::toRes);
    }

}
