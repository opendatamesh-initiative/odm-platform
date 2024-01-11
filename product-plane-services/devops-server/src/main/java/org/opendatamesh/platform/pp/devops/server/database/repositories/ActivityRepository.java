package org.opendatamesh.platform.pp.devops.server.database.repositories;

import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    class Specs {
        static public Specification<Activity> hasMatch(
            String dataProductId,
            String dataProductVersion,
            String stage,
            ActivityStatus status) {
			
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
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		    };
        }   
    }
}