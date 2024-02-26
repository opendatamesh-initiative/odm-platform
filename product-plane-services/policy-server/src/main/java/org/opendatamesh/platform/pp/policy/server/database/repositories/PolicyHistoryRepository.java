package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyHistory;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.repository.NoRepositoryBean;

//TODO change Id type when configured
@NoRepositoryBean
public interface PolicyHistoryRepository extends PagingAndSortingAndSpecificationExecutorRepository<PolicyHistory, String>  {
}
