package org.opendatamesh.platform.pp.registry.database.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DefinitionRepository extends JpaRepository<Definition, Long>, JpaSpecificationExecutor<Definition> {

    public boolean existsByNameAndVersion(String name, String version);

    class Specs {
        static public Specification<Definition> hasMatch(
            String name, String version, String type,
            String specification,
            String specificationVersion) {
			
            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (name != null) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), name));
                } 
                if (version != null) {
                    predicates.add(criteriaBuilder.equal(root.get("version"), version));
                }
                if (type != null) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }
                if (specification != null) {
                    predicates.add(criteriaBuilder.equal(root.get("specification"), specification));
                }
                    
                if (specificationVersion != null) {
                    predicates.add(criteriaBuilder.equal(root.get("specificationVersion"), specificationVersion));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		    };
        }   
    }
}
