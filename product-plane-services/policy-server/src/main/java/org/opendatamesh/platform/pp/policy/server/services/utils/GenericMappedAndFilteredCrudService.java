package org.opendatamesh.platform.pp.policy.server.services.utils;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public abstract class GenericMappedAndFilteredCrudService<F, R, T, ID extends Serializable> extends GenericMappedCrudService<R, T, ID> {
    @Autowired
    private PolicyTransactionHandler policyTransactionHandler;

    public final Page<R> findAllResourcesFiltered(Pageable pageable, F filters) {
        return policyTransactionHandler.runInTransaction(() ->
                findAllFiltered(pageable, filters).map(this::toRes)
        );
    }

    public final Page<T> findAllFiltered(Pageable pageable, F filters) {
        Specification<T> spec = getSpecFromFilters(filters);
        try {
            return getRepository().findAll(spec, pageable);
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    e.getMessage()
            );
        }
    }

    protected abstract PagingAndSortingAndSpecificationExecutorRepository<T, ID> getRepository();


    protected abstract Specification<T> getSpecFromFilters(F filters);

}
