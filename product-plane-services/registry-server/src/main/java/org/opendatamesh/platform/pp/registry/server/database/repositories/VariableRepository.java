package org.opendatamesh.platform.pp.registry.server.database.repositories;

import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.variables.Variable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface VariableRepository extends JpaRepository<Variable, Long>, JpaSpecificationExecutor<Variable> {

    class Specs {
        static public Specification<Variable> hasMatch(
                String dataProductId,
                String dataProductVersion,
                String variableName
        ) {

            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (dataProductId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("dataProductId"), dataProductId));
                }
                if (dataProductVersion != null) {
                    predicates.add(criteriaBuilder.equal(root.get("dataProductVersion"), dataProductVersion));
                }
                if (variableName != null) {
                    predicates.add(criteriaBuilder.equal(root.get("variableName"), variableName));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
    }

}