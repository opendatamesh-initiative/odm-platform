package org.opendatamesh.platform.up.policy.api.v1.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum Routes implements ODMApiRoutes {

    POLICYSERVICE_POLICY("/api/v1/up/policy-service/policies"),
    POLICYSERVICE_SUITE("/api/v1/up/policy-service/suites"),
    POLICYSERVICE_VALIDATE_BASEURL("/api/v1/up/policy-service/validate");

    private final String path;

    Routes(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.path;
    }

    public String getPath() {
        return path;
    }

}
