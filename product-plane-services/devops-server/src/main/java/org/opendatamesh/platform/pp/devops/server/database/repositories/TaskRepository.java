package org.opendatamesh.platform.pp.devops.server.database.repositories;

import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    
    List<Task> findByActivityId(Long activityId);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.activityId = :activityId")
    int deleteByActivityId(@Param("activityId") Long activityId);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.activityId IN (SELECT a.id FROM Activity a WHERE a.dataProductId = :dataProductId)")
    int deleteByDataProductId(@Param("dataProductId") String dataProductId);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.activityId IN (SELECT a.id FROM Activity a WHERE a.dataProductId = :dataProductId AND a.dataProductVersion = :version)")
    int deleteByDataProductIdAndVersion(@Param("dataProductId") String dataProductId, @Param("version") String version);
  
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