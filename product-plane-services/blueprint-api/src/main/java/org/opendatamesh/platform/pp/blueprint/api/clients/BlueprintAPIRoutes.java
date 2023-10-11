package org.opendatamesh.platform.pp.blueprint.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum BlueprintAPIRoutes implements ODMApiRoutes {

    BLUEPRINTS("/blueprints");

    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/pp/blueprint";

    BlueprintAPIRoutes(String path) {
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
