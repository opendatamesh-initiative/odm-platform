package org.opendatamesh.platform.pp.registry.server.database.repositories;

import org.opendatamesh.platform.pp.registry.server.database.entities.Api;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface ApiRepository extends JpaRepository<Api, String>, JpaSpecificationExecutor<Api> {

    public boolean existsByNameAndVersion(String name, String version);

    class Specs {
        static public Specification<Api> hasMatch(
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
