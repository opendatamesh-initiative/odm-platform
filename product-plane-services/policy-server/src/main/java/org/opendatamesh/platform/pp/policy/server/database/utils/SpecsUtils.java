package org.opendatamesh.platform.pp.policy.server.database.utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class SpecsUtils {

	public static final char LIKE_ESCAPE_CHAR = '\\';

	public static <T> Specification<T> combineWithAnd(List<Specification<T>> specs) {
		return (root, query, cb) -> cb.and(
				specificationsToPredicateArray(specs, root, query, cb)
		);
	}

	private static <T> Predicate[] specificationsToPredicateArray(List<Specification<T>> specs, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return specs.stream()
				.map(s -> s.toPredicate(root, query, cb))
				.toArray(Predicate[]::new);
	}

	public static <T> Specification<T> combineWithOr(List<Specification<T>> specs) {
		if (specs.isEmpty()) {
			//return always true 1=1
			return combineWithAnd(specs);
		}
		return (root, query, cb) -> cb.or(
				specificationsToPredicateArray(specs, root, query, cb)
		);
	}

	public static String escapeLikeParameter(String likeParameter, char escapeChar) {
		return likeParameter
				.replace("\\", "\\\\")
				.replace("_", escapeChar + "_")
				.replace("%", escapeChar + "%");
	}

	public static <T> Specification<T> falseSpec() {
		return (root, query, cb) -> cb.or();
	}
}
