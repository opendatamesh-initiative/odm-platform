package org.opendatamesh.platform.pp.registry.api.v1.clients;

import org.opendatamesh.platform.core.commons.clients.RoutesInterface;

public enum Routes implements RoutesInterface {

    DATA_PRODUCTS("/api/v1/pp/products"),

    APIS("/api/v1/pp/apis"),

    TEMPLATES("/api/v1/pp/templates"),
    SCHEMAS("/api/v1/pp/schemas"),
    
    DATA_PRODUCTS_UPLOADS("/api/v1/pp/products/uploads"),
    
    DATA_PRODUCTS_LISTURL("/api/v1/pp/products"),
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
