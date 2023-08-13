package org.opendatamesh.platform.pp.registry.api.v1.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum RegistryAPIRoutes implements ODMApiRoutes {

    DATA_PRODUCTS("/products"),
    APIS("/apis"),
    SCHEMAS("/schemas"),
    TEMPLATES("/templates"),
    DATA_PRODUCTS_UPLOADS("/uploads");


    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/pp/registry";

    RegistryAPIRoutes(String path) {
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
