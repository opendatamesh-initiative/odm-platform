package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.repository.NoRepositoryBean;

//TODO change Id type when configured
@NoRepositoryBean
public interface PolicyRepository extends PagingAndSortingAndSpecificationExecutorRepository<Policy, String> {
}
