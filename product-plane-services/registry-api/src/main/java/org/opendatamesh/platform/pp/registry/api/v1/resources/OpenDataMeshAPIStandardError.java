package org.opendatamesh.platform.pp.registry.api.v1.resources;

public enum OpenDataMeshAPIStandardError {

    // Bad Request Exceptions
    SC400_01_DESCRIPTOR_IS_EMPTY ("40001", "Descriptor is empty"),
    SC400_03_DOMAIN_IS_EMPTY ("40003", "Domain is empty"),
    SC400_04_INVALID_FORMAT ("40004", "Invalid format"),
    SC400_05_INVALID_URILIST ("40005", "Invalid uri-list"),
    SC400_06_INVALID_PORTTYPE ("40006", "Invalid port type"),
    SC400_07_PRODUCT_ID_IS_EMPTY ("40007", "Product id is empty"),
    SC400_08_STDDEF_IS_EMPTY ("40008", "Standard definition is empty"),
    SC400_09_STDDEF_ID_IS_EMPTY ("40009", "Standard definition id is empty"),
    SC400_10_PRODUCT_IS_EMPTY ("40010", "Data product is empty"),
    SC400_11_PRODUCT_VERSION_NUMBER_IS_EMPTY ("40011", "Data product version number is empty"),
    SC400_12_SCHEMA_IS_EMPTY ("40012", "Schema is empty"),
    SC400_13_SCHEMA_ID_IS_EMPTY ("40013", "Schema id is empty"),
    SC400_14_TEMPLATE_IS_EMPTY ("40014", "Template is empty"),
    SC400_15_TEMPLATE_ID_IS_EMPTY ("40015", "Template id is empty"),

    SC400_99_PROPERTY_REF_EXCEPTION ("40099", "Property Reference Exception"),
   
    // Not Found Exceptions
    SC404_01_PRODUCT_NOT_FOUND ("40401", "Data product not found"),
    SC404_02_VERSION_NOT_FOUND ("40402", "Data product version not found"),
    SC404_03_STDDEF_NOT_FOUND ("40403", "Standard definition not found"),
    SC404_04_SCHEMA_NOT_FOUND ("40404", "Schema not found"),
    SC404_05_TEMPLATE_NOT_FOUND ("40405", "Template not found"),

    // Unprocessable Entity Exceptions
    SC422_01_DESCRIPTOR_URI_IS_INVALID ("42201", "Descriptor URI is invalid"),
    SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID ("42202", "Descriptor document syntax is invalid"),
    SC422_03_DESCRIPTOR_DOC_SEMANTIC_IS_INVALID ("42203", "Descriptor document semantic is invalid"),
    SC422_04_PRODUCT_ALREADY_EXISTS ("42204", "Data product already exists"),
    SC422_05_VERSION_ALREADY_EXISTS ("42205", "Version already exists"),
    SC422_06_STDDEF_ALREADY_EXISTS ("42206", "Standard definition already exists"),
    SC422_07_PRODUCT_IS_INVALID ("42207", "Data product is invalid"),
    SC422_08_DEFINITION_DOC_SYNTAX_IS_INVALID ("42208", "Definition document is invalid"),
    SC422_09_DEFINITION_ALREADY_EXISTS ("42209", "Definition alredy exists"),

    SC422_10_SCHEMA_SYNTAX_IS_INVALID ("42210", "Schema is invalid"),
    SC422_11_SCHEMA_ALREADY_EXISTS ("42211", "Schema already exists"),
    SC422_12_SCHEMA_TO_API_REL_ALREADY_EXISTS ("42212", "Schema to api relationship alredy exists"),
    SC422_13_TEMPLATE_ALREADY_EXISTS ("42213", "Template already exists"),
    SC422_14_TEMPLATE_DOC_SYNTAX_IS_INVALID ("42214", "Template document is invalid"),

    // Conflict Exceptions
    SC409_01_CONCURRENT_DEPLOYMENT ("40901", "Concurrent deployment task"),

    // Bad Gateway Exceptions
    SC502_01_POLICY_SERVICE_ERROR ("50201", "Invalid policyService's response"),
    SC502_02_PROVISION_SERVICE_ERROR ("50202", "Invalid provisionService's response"),
    SC502_03_BUILD_SERVICE_ERROR ("50203", "Invalid buildService's response"),
    SC502_04_DEPLOY_SERVICE_ERROR ("50204", "Invalid deployService's response"),
    SC502_05_META_SERVICE_ERROR ("50205", "Invalid metaService's response"),

    // Internal Server Exceptions
    SC500_00_SERVICE_ERROR ("50000", "Error in in the backend service"),
    SC500_01_DATABASE_ERROR ("50001", "Error in the backend database"),
    SC500_02_DESCRIPTOR_ERROR ("50002", "Error in the backend descriptor processor"),

    SC000_TBD ("00000", "TBD");
    
    private final String code;   
    private final String description; 
    
    OpenDataMeshAPIStandardError(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}