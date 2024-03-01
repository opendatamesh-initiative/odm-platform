package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.springframework.data.repository.NoRepositoryBean;

public interface PolicyEvaluationResultRepository extends PagingAndSortingAndSpecificationExecutorRepository<PolicyEvaluationResult, Long> {
}
