package org.opendatamesh.platform.up.policy.api.v1.clients;

import org.opendatamesh.platform.core.commons.clients.RoutesInterface;

public enum Routes implements RoutesInterface {

    POLICYSERVICE_POLICY("/api/v1/planes/utility/policy-services/opa/policies"),
    POLICYSERVICE_SUITE("/api/v1/planes/utility/policy-services/opa/suites"),
    POLICYSERVICE_VALIDATE_BASEURL("/api/v1/planes/utility/policy-services/opa/validate");

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
