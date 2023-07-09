package org.opendatamesh.platform.pp.registry.database.repositories;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.TemplateDefinition;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface TemplateDefinitionRepository extends JpaRepository<TemplateDefinition, Long>, JpaSpecificationExecutor<TemplateDefinition> {

    public boolean existsByNameAndVersion(String name, String version);

    class Specs {
        static public Specification<TemplateDefinition> hasMatch(
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
