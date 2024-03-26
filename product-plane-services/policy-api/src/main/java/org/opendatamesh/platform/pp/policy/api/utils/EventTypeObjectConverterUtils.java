package org.opendatamesh.platform.pp.policy.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource.EventType;
import org.opendatamesh.platform.pp.policy.api.resources.events.ActivityResultEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.ActivityStageTransitionEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.DataProductEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.resources.events.TaskResultEventTypeResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;

public final class EventTypeObjectConverterUtils {

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    private static final ImmutableMap<EventType, Class<?>> eventTypeClassMap = ImmutableMap.of(
            EventType.DATA_PRODUCT_CREATION, DataProductEventTypeResource.class,
            EventType.DATA_PRODUCT_UPDATE, DataProductEventTypeResource.class,
            EventType.ACTIVITY_STAGE_TRANSITION, ActivityStageTransitionEventTypeResource.class,
            EventType.TASK_EXECUTION_RESULT, TaskResultEventTypeResource.class,
            EventType.ACTIVITY_EXECUTION_RESULT, ActivityResultEventTypeResource.class
    );

    public static <T> T convertJsonNode(JsonNode jsonNode, EventType eventType) {
        Class<?> jsonNodeClass = eventTypeClassMap.get(eventType);
        if(jsonNodeClass == null) {
            throw new BadRequestException(
                    PolicyApiStandardErrors.SC400_04_UNKNOWN_EVENT,
                    "Unknown event [" + eventType + "]"
            );
        }
        return convertJsonNodeToClass(jsonNode, jsonNodeClass);
    }

    private static <T> T convertJsonNodeToClass(JsonNode jsonNode, Class<?> jsonNodeClass) {
        try {
            return objectMapper.convertValue(jsonNode, (Class<T>) jsonNodeClass);
        } catch (IllegalArgumentException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Serialization error converting JSON object to " + jsonNodeClass
            );
        }
    }

}
