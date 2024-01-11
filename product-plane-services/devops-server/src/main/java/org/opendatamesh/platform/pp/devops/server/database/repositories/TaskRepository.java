package org.opendatamesh.platform.pp.devops.server.database.repositories;

import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
  
    class Specs {
        static public Specification<Task> hasMatch(
            Long activityId,
            String executorRef, 
            ActivityTaskStatus status) {
			
            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (activityId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("activityId"), activityId));
                } 
                if (executorRef != null) {
                    predicates.add(criteriaBuilder.equal(root.get("executorRef"), executorRef));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		    };
        }   
    }
}