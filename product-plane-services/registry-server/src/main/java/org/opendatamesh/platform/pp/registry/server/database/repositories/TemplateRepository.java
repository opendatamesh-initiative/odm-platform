package org.opendatamesh.platform.pp.registry.server.database.repositories;

import org.opendatamesh.platform.pp.registry.server.database.entities.Template;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, String>, JpaSpecificationExecutor<Template> {

    public boolean existsByNameAndVersion(String name, String version);
    
    public List<Template> findByOldId(String oldId);

    class Specs {
        static public Specification<Template> hasMatch(
            String name, 
            String version, 
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
