package org.opendatamesh.platform.pp.policy.server.database.mappers;

import com.google.common.collect.ImmutableMap;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;

import java.util.Map;
import java.util.Optional;

public class EntitiesToResources {
    private EntitiesToResources() {
    }

    private static final Map<String, String> entityToResourceMap = ImmutableMap
            .<String, String>builder()
            .put(PolicyEngine.class.getName(), PolicyEngineResource.class.getName())
            .put(Policy.class.getName(), PolicyResource.class.getName())
            .put(PolicyEvaluationResult.class.getName(), PolicyEvaluationResultResource.class.getName())
            .build();

    public static <T> String getResourceClassName(Class<T> entityClass) {

        return Optional.ofNullable(entityToResourceMap.get(entityClass.getName()))
                .orElseThrow(() -> new InternalServerException("Entity " + entityClass.getName() + " not mapped to a corresponding resource."));
    }
}
