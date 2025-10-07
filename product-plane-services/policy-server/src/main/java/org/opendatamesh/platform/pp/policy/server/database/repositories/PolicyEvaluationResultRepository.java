package org.opendatamesh.platform.pp.policy.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PolicyEvaluationResultRepository extends PagingAndSortingAndSpecificationExecutorRepository<PolicyEvaluationResult, Long> {
    Optional<PolicyEvaluationResult> findTopByOrderByCreatedAtDesc();

    @Modifying
    @Query("DELETE FROM PolicyEvaluationResults p WHERE p.dataProductId = :dataProductId")
    int deleteByDataProductId(@Param("dataProductId") String dataProductId);

    @Modifying
    @Query("DELETE FROM PolicyEvaluationResults p WHERE p.dataProductId = :dataProductId AND p.dataProductVersion = :version")
    int deleteByDataProductIdAndVersion(@Param("dataProductId") String dataProductId, @Param("version") String version);

    class Specs extends SpecsUtils {
        public static Specification<PolicyEvaluationResult> hasDataProductVersion(String dataProductVersion) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PolicyEvaluationResult_.dataProductVersion), dataProductVersion));
        }

        public static Specification<PolicyEvaluationResult> hasDataProductId(String dataProductIdentifier) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PolicyEvaluationResult_.dataProductId), dataProductIdentifier));
        }
    }
}
