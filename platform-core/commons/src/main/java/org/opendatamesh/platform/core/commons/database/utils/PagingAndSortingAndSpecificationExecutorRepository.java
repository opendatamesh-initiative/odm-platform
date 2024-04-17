package org.opendatamesh.platform.core.commons.database.utils;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

@NoRepositoryBean
public interface PagingAndSortingAndSpecificationExecutorRepository<T, ID extends Serializable>
        extends PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T> {
}
