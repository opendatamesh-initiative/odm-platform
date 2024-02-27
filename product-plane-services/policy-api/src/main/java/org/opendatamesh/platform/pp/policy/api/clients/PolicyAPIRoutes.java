package org.opendatamesh.platform.pp.policy.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum PolicyAPIRoutes implements ODMApiRoutes {

    POLICIES("/policies"),
    ENGINES("/policy-engines"),
    RESULTS("/policy-evaluation-results");

    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/pp/policy";

    PolicyAPIRoutes(String path) {
        this.path = CONTEXT_PATH + path;
    }

    @Override
    public String toString() {
        return this.path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
