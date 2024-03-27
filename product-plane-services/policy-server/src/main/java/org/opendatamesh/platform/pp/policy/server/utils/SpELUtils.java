package org.opendatamesh.platform.pp.policy.server.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource.EventType;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public final class SpELUtils {

    private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();

    private static final Logger LOGGER = LoggerFactory.getLogger(SpELUtils.class);

    public static Boolean evaluateSpELExpression(JsonNode inputObject, String spelExpression, EventType eventType) {

        // Parse SpEL expression
        Expression expression = SPEL_PARSER.parseExpression(spelExpression);

        // Create context
        //Map<String, Object> context = convertJsonNodeToMap(inputObject); // Needed if Java Class of inputObject is unknown
        Object context;
        try {
            context = EventTypeObjectConverterUtils.convertJsonNode(inputObject, eventType);
        } catch (Exception e) {
            throw new BadRequestException(
                    PolicyApiStandardErrors.SC400_05_MALFORMED_INPUT_OBJECT,
                    "Malformed input Object for event [" + eventType.toString() + "]",
                    e
            );
        }

        // Evaluate expression
        Boolean evaluationResult = false;
        try {
            evaluationResult = expression.getValue(context, Boolean.class);
        } catch (EvaluationException e) {
            LOGGER.error(
                    "Error evaluating SpEL Expression [" + spelExpression
                            + "]. Policy will be filtered out from set of policies to be evaluated."
                            + "Error: " + e.getMessage()
            );
        }

        return evaluationResult;

    }

    /*private static Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode fieldValue = jsonNode.get(fieldName);
            if (fieldValue.isObject()) {
                map.put(fieldName, convertJsonNodeToMap(fieldValue));
            } else {
                map.put(fieldName, fieldValue.asText());
            }
        }
        return map;
    }*/

}
