package org.opendatamesh.platform.pp.policy.server.services.utils;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.util.function.Function;

public abstract class GenericCrudService<T, ID extends Serializable> {
    @Autowired
    private TransactionTemplate transactionTemplate;

    protected GenericCrudService() {

    }

    public final Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    public final T findOne(ID identifier) {
        T result = getRepository().findById(identifier).orElse(null);
        if (result == null) {
            throw new NotFoundException(PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND, "Resource with id=" + identifier + " not found");
        }
        afterFindOne(result, identifier);
        return result;
    }

    protected void afterFindOne(T foundObject, ID identifier) {

    }

    public final T create(T objectToCreate) {
        T result = transactionTemplate.execute(status -> {
            if (getIdentifier(objectToCreate) != null) {
                throw new BadRequestException(PolicyApiStandardErrors.SC400_01_MALFORMED_RESOURCE, "The resource to create has already an identifier");
            }
            validate(objectToCreate);
            reconcile(objectToCreate);
            beforeCreation(objectToCreate);
            T r = getRepository().save(objectToCreate);
            afterCreation(objectToCreate, r);
            return r;
        });
        afterCreationCommit(result);
        return result;
    }

    protected void afterCreationCommit(T createdEntity) {

    }

    protected void afterCreation(T objectToCreate, T result) {

    }

    protected void beforeCreation(T objectToCreate) {

    }

    public final T overwrite(ID identifier, T objectToOverwrite) {
        T overwrittenObject = transactionTemplate.execute(status -> {
            if (getIdentifier(objectToOverwrite) != identifier) {
                throw new BadRequestException(PolicyApiStandardErrors.SC400_01_MALFORMED_RESOURCE, "The resource to overwrite has an identifier that is different from the given one.");
            }
            validate(objectToOverwrite);
            checkExistenceOrThrow(identifier);
            reconcile(objectToOverwrite);
            beforeOverwrite(objectToOverwrite);
            T result = getRepository().save(objectToOverwrite);
            afterOverWrite(objectToOverwrite, result);
            return result;
        });
        afterOverwriteCommit(overwrittenObject);
        return overwrittenObject;
    }

    protected void afterOverwriteCommit(T overwrittenObject) {

    }

    protected void beforeOverwrite(T objectToOverwrite) {

    }

    protected void afterOverWrite(T objectToOverwrite, T result) {

    }

    public final void delete(ID identifier) {
        transactionTemplate.executeWithoutResult(status -> {
            checkExistenceOrThrow(identifier);
            beforeDelete(identifier);
            getRepository().deleteById(identifier);
            afterDelete(identifier);
        });
    }

    public final <R> R deleteReturning(ID identifier, Function<T, R> mapper) {
        T entity = findOne(identifier);
        R resource = mapper.apply(entity);
        delete(identifier);
        afterDeleteCommit(entity);
        return resource;
    }

    protected void afterDeleteCommit(T entity) {

    }

    public final void checkExistenceOrThrow(ID identifier) {
        if (!exists(identifier)) {
            throw new NotFoundException(PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND, "Resource with id=" + identifier + " not found");
        }
    }

    protected abstract PagingAndSortingRepository<T, ID> getRepository();

    protected void afterDelete(ID identifier) {

    }

    protected void beforeDelete(ID identifier) {

    }

    protected boolean exists(ID identifier) {
        return getRepository().existsById(identifier);
    }

    protected abstract void validate(T objectToValidate);

    protected abstract void reconcile(T objectToReconcile);

    protected abstract ID getIdentifier(T object);

}
