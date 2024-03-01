package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy_;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.SpecsUtils;
import org.springframework.data.jpa.domain.Specification;

public interface PolicyRepository extends PagingAndSortingAndSpecificationExecutorRepository<Policy, Long> {

    Policy findByRootIdAndIsLastVersionTrue(Long rootId);

    boolean existsByNameAndRootIdNot(String name, Long rootId);

    boolean existsByName(String name);

    class Specs extends SpecsUtils {
        public static Specification<Policy> hasLastVersion(Boolean lastVersion) {
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Policy_.isLastVersion), lastVersion)
            );
        }
    }
}
