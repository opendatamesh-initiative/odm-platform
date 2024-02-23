package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.repository.NoRepositoryBean;

//TODO change id type when configured
@NoRepositoryBean
public interface PolicyEngineRepository extends PagingAndSortingAndSpecificationExecutorRepository<PolicyEngine, String> {
}
