package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult_;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.SpecsUtils;
import org.springframework.data.jpa.domain.Specification;

public interface PolicyEvaluationResultRepository extends PagingAndSortingAndSpecificationExecutorRepository<PolicyEvaluationResult, Long> {
    class Specs extends SpecsUtils {
        public static Specification<PolicyEvaluationResult> hasDataProductVersion(String dataProductVersion) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PolicyEvaluationResult_.dataProductVersion), dataProductVersion));
        }

        public static Specification<PolicyEvaluationResult> hasDataProductId(String dataProductIdentifier) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PolicyEvaluationResult_.dataProductId), dataProductIdentifier));
        }
    }
}
