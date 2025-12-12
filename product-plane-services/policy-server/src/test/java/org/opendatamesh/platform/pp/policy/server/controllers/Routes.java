package org.opendatamesh.platform.pp.policy.server.controllers;

public enum Routes {

    POLICIES("/api/v1/pp/policy/policies"),
    POLICY_ENGINES("/api/v1/pp/policy/policy-engines"),
    POLICY_EVALUATION_RESULTS("/api/v1/pp/policy/policy-evaluation-results"),
    VALIDATION("/api/v1/pp/policy/validation");

    private final String path;

    Routes(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

