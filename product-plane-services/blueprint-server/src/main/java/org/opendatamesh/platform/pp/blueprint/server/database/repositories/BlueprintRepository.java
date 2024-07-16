package org.opendatamesh.platform.pp.blueprint.server.database.repositories;

import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.pp.blueprint.server.database.entities.Blueprint;
import org.opendatamesh.platform.pp.blueprint.server.database.entities.Blueprint_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface BlueprintRepository extends JpaRepository<Blueprint, Long>, JpaSpecificationExecutor<Blueprint> {

    class Specs extends SpecsUtils {
        public static Specification<Blueprint> hasMatch(String repositoryUrl, String blueprintDirectory) {

            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (repositoryUrl != null) {
                    predicates.add(criteriaBuilder.equal(root.get("repositoryUrl"), repositoryUrl));
                }
                if (blueprintDirectory != null) {
                    predicates.add(criteriaBuilder.equal(root.get("blueprintDirectory"), blueprintDirectory));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }

        public static Specification<Blueprint> search(String search) {
            return ((root, query, cb) -> {
                final String pattern = String.format("%%%s%%", escapeLikeParameter(search.toLowerCase(), LIKE_ESCAPE_CHAR));
                query.orderBy(
                        cb.asc(cb.length(cb.concat(
                                cb.concat(root.get(Blueprint_.projectId), ".")
                                , root.get(Blueprint_.name)
                        )))
                );
                return cb.or(
                        cb.like(cb.lower(root.get(Blueprint_.name)), pattern, LIKE_ESCAPE_CHAR),
                        cb.like(cb.lower(root.get(Blueprint_.projectId)), pattern, LIKE_ESCAPE_CHAR),
                        cb.like(
                                cb.lower(
                                        cb.concat(
                                                cb.concat(root.get(Blueprint_.projectId), ".")
                                                , root.get(Blueprint_.name)
                                        )
                                )
                                , pattern, LIKE_ESCAPE_CHAR)
                );
            });
        }


    }

}
