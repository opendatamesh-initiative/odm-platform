package org.opendatamesh.dpexperience.api;

public enum RoutesV1 {

    DATA_PRODUCTS("/api/v1/pp/products"),
    
    DATA_PRODUCTS_UPLOADS("/api/v1/pp/products/uploads"),
    
    DATA_PRODUCTS_LISTURL("/api/v1/pp/products"),
    POLICYVALIDATE_BASEURL("/api/v1/planes/utility/policy-services/opa/validate");

    private final String path;

    private RoutesV1(String path) {
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
