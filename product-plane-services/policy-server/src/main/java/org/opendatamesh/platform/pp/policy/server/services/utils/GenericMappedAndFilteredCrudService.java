package org.opendatamesh.platform.pp.policy.server.services.utils;

import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

public abstract class GenericMappedAndFilteredCrudService<F, R, T, ID extends Serializable> extends GenericMappedCrudService<R, T, ID> {

    public final Page<R> findAllResourcesFiltered(Pageable pageable, F filters) {
        return findAllFiltered(pageable, filters).map(this::toRes);
    }

    public final Page<T> findAllFiltered(Pageable pageable, F filters) {
        Specification<T> spec = getSpecFromFilters(filters);
        return getRepository().findAll(spec, pageable);
    }

    protected abstract PagingAndSortingAndSpecificationExecutorRepository<T, ID> getRepository();


    protected abstract Specification<T> getSpecFromFilters(F filters);

}
