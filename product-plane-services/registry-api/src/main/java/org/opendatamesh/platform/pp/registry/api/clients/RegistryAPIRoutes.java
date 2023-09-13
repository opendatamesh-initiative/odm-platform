package org.opendatamesh.platform.pp.registry.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum RegistryAPIRoutes implements ODMApiRoutes {

    DATA_PRODUCTS("/products"),
    DATA_PRODUCT_VERSIONS_APPLICATIONS("/products/{id}/versions/{version}/applications"),
    DATA_PRODUCT_VERSIONS_INFRASTRUCTURES("/products/{id}/versions/{version}/infrastructures"),
    APIS("/apis"),
    SCHEMAS("/schemas"),
    TEMPLATES("/templates"),
    DATA_PRODUCTS_UPLOADS("/uploads"),
    DOMAINS("/domains"),
    OWNERS("/owners");


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
