package org.opendatamesh.platform.pp.registry.server.database.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.DataProduct;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataProductRepository
        extends JpaRepository<DataProduct, String>, JpaSpecificationExecutor<DataProduct> {

    class Specs {
        static public Specification<DataProduct> hasMatch(
                String fqn,
                String domain) {

            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (fqn != null) {
                    predicates.add(criteriaBuilder.equal(root.get("fullyQualifiedName"), fqn));
                }
                if (domain != null) {
                    predicates.add(criteriaBuilder.equal(root.get("domain"), domain));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
    }
}
