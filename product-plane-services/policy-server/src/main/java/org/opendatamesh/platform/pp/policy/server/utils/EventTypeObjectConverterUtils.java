package org.opendatamesh.platform.pp.policy.server.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource.EventType;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.resources.ActivityResultEvent;
import org.opendatamesh.platform.pp.policy.server.resources.ActivityStageTransitionEvent;
import org.opendatamesh.platform.pp.policy.server.resources.DataProductEvent;
import org.opendatamesh.platform.pp.policy.server.resources.TaskResultEvent;

public final class EventTypeObjectConverterUtils {

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true
    );

    private static final ImmutableMap<EventType, Class<?>> eventTypeClassMap = ImmutableMap.of(
            EventType.DATA_PRODUCT_CREATION, DataProductEvent.class,
            EventType.DATA_PRODUCT_UPDATE, DataProductEvent.class,
            EventType.DATA_PRODUCT_VERSION_CREATION, DataProductEvent.class,
            EventType.ACTIVITY_STAGE_TRANSITION, ActivityStageTransitionEvent.class,
            EventType.TASK_EXECUTION_RESULT, TaskResultEvent.class,
            EventType.ACTIVITY_EXECUTION_RESULT, ActivityResultEvent.class
    );

    public static <T> T convertJsonNode(JsonNode jsonNode, EventType eventType) {
        return convertJsonNodeToClass(jsonNode, getClassFromEventType(eventType));
    }

    private static Class<?> getClassFromEventType(EventType eventType) {
        Class<?> jsonNodeClass = eventTypeClassMap.get(eventType);
        if (jsonNodeClass == null) {
            throw new BadRequestException(
                    PolicyApiStandardErrors.SC400_04_UNKNOWN_EVENT,
                    "Unknown event [" + eventType + "]"
            );
        }
        return jsonNodeClass;
    }

    private static <T> T convertJsonNodeToClass(JsonNode jsonNode, Class<?> jsonNodeClass) {
        try {
            return objectMapper.convertValue(jsonNode, (Class<T>) jsonNodeClass);
        } catch (Exception e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Serialization error converting JSON object to " + jsonNodeClass,
                    e
            );
        }
    }

}
