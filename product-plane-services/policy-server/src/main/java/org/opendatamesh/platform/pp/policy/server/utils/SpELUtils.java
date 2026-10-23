package org.opendatamesh.platform.pp.policy.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import java.util.Map;

/**
 * Evaluates the filtering expression of a policy against the input of an evaluation request (BDMD-5438, DD-11).
 * <p>
 * Constraints of the filtering expressions:
 * <ul>
 *   <li>They are evaluated on the raw JSON input, whatever its shape. The input is not converted to Java types.</li>
 *   <li>They use the JSON field names, with dot navigation ({@code afterState.activity.name}) or the {@code ['key']}
 *   form. A missing key reads as {@code null}. Navigating into a {@code null} value is an error, which filters the
 *   policy out. Use {@code ?.} to navigate safely.</li>
 *   <li>Only JSON values are available: maps, lists, strings, numbers, booleans and {@code null}. Java getters of the
 *   old typed V1 classes are not available. Instance methods of these values can be called, for example
 *   {@code size()} or {@code startsWith('pr')}.</li>
 *   <li>{@code SimpleEvaluationContext} is used on purpose. Type references {@code T(...)}, constructors and bean
 *   references are not available, and calls to {@code getClass()} are rejected, which blocks the reflective route to
 *   Java classes. This limits what an expression can reach. It relies on the restrictions of Spring's
 *   {@code SimpleEvaluationContext} and is not a formal guarantee, so keep this configuration when changing the code.
 *   The expression is not bounded in time or size.</li>
 *   <li>A filter that fails to evaluate, or that does not produce a boolean, filters the policy out.</li>
 * </ul>
 */
public final class SpELUtils {

    private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(SpELUtils.class);

    public static boolean eventObjectMatchesSpelExpression(JsonNode inputObject, String spelExpression, EventType eventType) {

        Expression expression = SPEL_PARSER.parseExpression(spelExpression);
        Object rootObject = JSON_MAPPER.convertValue(inputObject, Object.class);

        try {
            Object result = expression.getValue(createEvaluationContext(), rootObject);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            LOGGER.error(
                    "SpEL Expression [" + spelExpression + "] for event [" + eventType
                            + "] does not evaluate to a boolean. Policy will be filtered out from set of policies to be evaluated."
            );
        } catch (EvaluationException e) {
            LOGGER.error(
                    "Error evaluating SpEL Expression [" + spelExpression + "] for event [" + eventType
                            + "]. Policy will be filtered out from set of policies to be evaluated. "
                            + "Error: " + e.getMessage()
            );
        }

        return false;
    }

    private static EvaluationContext createEvaluationContext() {
        return SimpleEvaluationContext
                .forPropertyAccessors(new JsonMapPropertyAccessor())
                .withInstanceMethods()
                .build();
    }

    /**
     * Reads a JSON field from a map. A missing key reads as {@code null}. It never writes.
     */
    private static final class JsonMapPropertyAccessor implements PropertyAccessor {

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class<?>[]{Map.class};
        }

        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) {
            return target instanceof Map;
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) {
            return new TypedValue(((Map<?, ?>) target).get(name));
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) {
            return false;
        }

        @Override
        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
            throw new AccessException("Filtering expressions are read-only");
        }
    }
}
