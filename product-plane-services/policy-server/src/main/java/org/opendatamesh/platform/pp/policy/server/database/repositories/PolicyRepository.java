package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;

public interface PolicyRepository extends PagingAndSortingAndSpecificationExecutorRepository<Policy, Long> {

    Policy findByRootIdAndIsLastVersionTrue(Long rootId);
}
