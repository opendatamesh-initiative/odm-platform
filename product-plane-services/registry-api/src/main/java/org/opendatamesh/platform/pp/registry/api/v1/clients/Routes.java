package org.opendatamesh.platform.pp.registry.api.v1.clients;

public enum Routes {

    DATA_PRODUCTS("/api/v1/pp/products"),

    APIS("/api/v1/pp/apis"),

    TEMPLATES("/api/v1/pp/templates"),
    SCHEMAS("/api/v1/pp/schemas"),
    
    DATA_PRODUCTS_UPLOADS("/api/v1/pp/products/uploads"),
    
    DATA_PRODUCTS_LISTURL("/api/v1/pp/products"),
    POLICYVALIDATE_BASEURL("/api/v1/planes/utility/policy-services/opa/validate"); 


    private final String path;

    private Routes(String path) {
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
