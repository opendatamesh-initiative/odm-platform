package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine_;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy_;
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

        public static Specification<Policy> hasEvaluationEvent(String evaluationEvent) {
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Policy_.evaluationEvent), evaluationEvent)
            );
        }

        public static Specification<Policy> hasEngineName(String policyEngineName) {
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Policy_.policyEngine).get(PolicyEngine_.name), policyEngineName)
            );
        }

        public static Specification<Policy> hasName(String name) {
            return ((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Policy_.name), name)
            );
        }
    }

}
