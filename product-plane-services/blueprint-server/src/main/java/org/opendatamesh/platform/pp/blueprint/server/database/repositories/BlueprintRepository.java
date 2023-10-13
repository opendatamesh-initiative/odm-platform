package org.opendatamesh.platform.pp.blueprint.server.database.repositories;

import org.opendatamesh.platform.pp.blueprint.server.database.entities.Blueprint;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface BlueprintRepository extends JpaRepository<Blueprint, Long>, JpaSpecificationExecutor<Blueprint> {

    class Specs {
        static public Specification<Blueprint> hasMatch(
                String repositoryBaseUrl,
                String blueprintRepo
        ) {

            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (repositoryBaseUrl != null) {
                    predicates.add(criteriaBuilder.equal(root.get("repositoryBaseUrl"), repositoryBaseUrl));
                }
                if (blueprintRepo != null) {
                    predicates.add(criteriaBuilder.equal(root.get("blueprintRepo"), blueprintRepo));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
    }

}
