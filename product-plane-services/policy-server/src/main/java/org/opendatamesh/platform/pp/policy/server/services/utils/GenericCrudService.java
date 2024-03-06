package org.opendatamesh.platform.pp.policy.server.services.utils;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.policy.server.database.utils.ErrorCodeMapper;
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

    private Class<T> classType;

    protected GenericCrudService(Class<T> classType) {
        this.classType = classType;
    }

    //READ METHODS

    public final Page<T> findAll(Pageable pageable) {
        try {
            return getRepository().findAll(pageable);
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    e.getMessage()
            );
        }
    }

    public final T findOne(ID identifier) {
        T result;
        try {
            result = findById(identifier);
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    e.getMessage()
            );
        }
        if (result == null) {
            throw new NotFoundException(
                    ErrorCodeMapper.getErrorCodeForClass(classType),
                    ErrorCodeMapper.getErrorMessageForClass(classType, identifier)
            );
        }
        afterFindOne(result, identifier);
        return result;
    }

    protected T findById(ID identifier) {
        try {
            return getRepository().findById(identifier).orElse(null);
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    e.getMessage()
            );
        }
    }

    protected void afterFindOne(T foundObject, ID identifier) {

    }

    public final void checkExistenceOrThrow(ID identifier) {
        if (!exists(identifier)) {
            throw new NotFoundException(
                    ErrorCodeMapper.getErrorCodeForClass(classType),
                    ErrorCodeMapper.getErrorMessageForClass(classType, identifier)
            );
        }
    }

    //CREATE METHODS

    public final T create(T objectToCreate) {
        T result = transactionTemplate.execute(status -> {
            validate(objectToCreate);
            reconcile(objectToCreate);
            beforeCreation(objectToCreate);
            T r;
            try {
                r = getRepository().save(objectToCreate);
            } catch (Exception e) {
                throw new InternalServerException(
                        ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                        e.getMessage()
                );
            }
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

    //UPDATE METHODS

    public final T overwrite(ID identifier, T objectToOverwrite) {
        T overwrittenObject = transactionTemplate.execute(status -> {
            validate(objectToOverwrite);
            checkExistenceOrThrow(identifier);
            reconcile(objectToOverwrite);
            beforeOverwrite(objectToOverwrite);
            T result;
            try {
                result = getRepository().save(objectToOverwrite);
            } catch (Exception e) {
                throw new InternalServerException(
                        ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                        e.getMessage()
                );
            }
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

    //DELETE METHODS

    public final void delete(ID identifier) {
        transactionTemplate.executeWithoutResult(status -> {
            checkExistenceOrThrow(identifier);
            beforeDelete(identifier);
            try {
                getRepository().deleteById(identifier);
            } catch (Exception e) {
                throw new InternalServerException(
                        ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                        e.getMessage()
                );
            }
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

    protected void afterDelete(ID identifier) {

    }

    protected void beforeDelete(ID identifier) {

    }

    //UTILS

    protected boolean exists(ID identifier) {
        try {
            return getRepository().existsById(identifier);
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    e.getMessage()
            );
        }
    }

    protected abstract PagingAndSortingRepository<T, ID> getRepository();

    protected abstract void validate(T objectToValidate);

    protected abstract void reconcile(T objectToReconcile);

}