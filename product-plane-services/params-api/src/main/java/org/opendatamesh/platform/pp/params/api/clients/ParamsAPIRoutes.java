package org.opendatamesh.platform.pp.params.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum ParamsAPIRoutes implements ODMApiRoutes {

    PARAMS("/params"),

    PARAMS_FILTER("/params/filter");

    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/pp/params";

    ParamsAPIRoutes(String path) {
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
