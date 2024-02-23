package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicySuite;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.repository.NoRepositoryBean;

//TODO change id type when configured
@NoRepositoryBean
public interface PolicySuiteRepository extends PagingAndSortingAndSpecificationExecutorRepository<PolicySuite, String> {
}
