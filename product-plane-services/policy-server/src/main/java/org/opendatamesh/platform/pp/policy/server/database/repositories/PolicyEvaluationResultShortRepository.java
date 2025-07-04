package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResultShort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyEvaluationResultShortRepository extends PagingAndSortingRepository<PolicyEvaluationResultShort, Long>, JpaSpecificationExecutor<PolicyEvaluationResultShort>, PagingAndSortingAndSpecificationExecutorRepository<PolicyEvaluationResultShort, Long> {

    class Specs {
        public static Specification<PolicyEvaluationResultShort> hasDataProductId(String dataProductId) {
            return (root, query, criteriaBuilder) -> {
                if (dataProductId == null || dataProductId.isEmpty()) {
                    return criteriaBuilder.conjunction();
                }
                return criteriaBuilder.equal(root.get("dataProductId"), dataProductId);
            };
        }
    }
} 