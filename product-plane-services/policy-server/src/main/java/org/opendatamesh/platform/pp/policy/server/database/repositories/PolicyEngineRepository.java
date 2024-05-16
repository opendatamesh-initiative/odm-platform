package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;

public interface PolicyEngineRepository extends PagingAndSortingAndSpecificationExecutorRepository<PolicyEngine, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, Long id);

    PolicyEngine findByName(String name);
}
