package org.opendatamesh.platform.up.validator.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum ValidatorAPIRoutes implements ODMApiRoutes {

    EVALUATE_POLICY("/evaluate-policy");

    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/up/validator";

    ValidatorAPIRoutes(String path) {
        this.path = CONTEXT_PATH + path;
    }

    public String getPath() {
        return path;
    }

}