package org.opendatamesh.platform.pp.devops.server.database.repositories;

import org.opendatamesh.platform.pp.devops.server.database.entities.Lifecycle;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface LifecycleRepository extends JpaRepository<Lifecycle, Long>, JpaSpecificationExecutor<Lifecycle> {

    List<Lifecycle> findByDataProductId(String dataProductId);

    List<Lifecycle> findByDataProductIdAndVersionNumber(String dataProductId, String versionNumber);

    Lifecycle findByDataProductIdAndVersionNumberAndFinishedAtIsNull(String dataProductId, String versionNumber);

    // FORSE ANCHE LA DATA? MA SERVE?
    class Specs {
        static public Specification<Lifecycle> hasMatch(
                String dataProductId,
                String dataProductVersion,
                String stage
        ) {

            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (dataProductId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("dataProductId"), dataProductId));
                }
                if (dataProductVersion != null) {
                    predicates.add(criteriaBuilder.equal(root.get("dataProductVersion"), dataProductVersion));
                }
                if (stage != null) {
                    predicates.add(criteriaBuilder.equal(root.get("stage"), stage));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
    }
    
}
