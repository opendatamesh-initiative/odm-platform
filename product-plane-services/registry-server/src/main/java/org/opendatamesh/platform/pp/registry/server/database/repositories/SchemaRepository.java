package org.opendatamesh.platform.pp.registry.server.database.repositories;

import org.opendatamesh.platform.pp.registry.server.database.entities.Schema;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface SchemaRepository extends JpaRepository<Schema, Long>, JpaSpecificationExecutor<Schema> {

    public boolean existsByNameAndVersion(String name, String version);

    class Specs {
        static public Specification<Schema> hasMatch(
            String name, String version) {
			
            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (name != null) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), name));
                } 
                if (version != null) {
                    predicates.add(criteriaBuilder.equal(root.get("version"), version));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		    };
        }   
    }
}
