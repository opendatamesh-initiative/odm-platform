package org.opendatamesh.platform.pp.registry.api.resources;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum RegistryApiStandardErrors implements ODMApiStandardErrors {

    // Bad Request Exceptions
    SC400_01_DESCRIPTOR_IS_EMPTY ("40001", "Descriptor is empty"),
    SC400_03_DOMAIN_IS_EMPTY ("40003", "Domain is empty"),
    SC400_04_INVALID_FORMAT ("40004", "Invalid format"),
    SC400_05_INVALID_URILIST ("40005", "Invalid uri-list"),
    SC400_06_INVALID_PORTTYPE ("40006", "Invalid port type"),
    SC400_07_PRODUCT_ID_IS_EMPTY ("40007", "Product id is empty"),
    SC400_10_PRODUCT_IS_EMPTY ("40010", "Data product is empty"),
    SC400_11_PRODUCT_VERSION_NUMBER_IS_EMPTY ("40011", "Data product version number is empty"),
    
    SC400_08_API_IS_EMPTY ("40008", "Standard definition is empty"),
    SC400_09_API_ID_IS_EMPTY ("40009", "Standard definition id is empty"),
    
    SC400_12_SCHEMA_IS_EMPTY ("40012", "Schema is empty"),
    SC400_13_SCHEMA_ID_IS_EMPTY ("40013", "Schema id is empty"),
    SC400_14_TEMPLATE_IS_EMPTY ("40014", "Template is empty"),
    SC400_15_TEMPLATE_ID_IS_EMPTY ("40015", "Template id is empty"),

    // Not Found Exceptions
    SC404_01_PRODUCT_NOT_FOUND ("40401", "Data product not found"),
    SC404_02_VERSION_NOT_FOUND ("40402", "Data product version not found"),
    SC404_03_API_NOT_FOUND ("40403", "Api not found"),
    SC404_04_SCHEMA_NOT_FOUND ("40404", "Schema not found"),
    SC404_05_TEMPLATE_NOT_FOUND ("40405", "Template not found"),

    // Unprocessable Entity Exceptions
    SC422_01_DESCRIPTOR_URI_NOT_VALID ("42201", "Descriptor URI is not valid"),
    SC422_02_DESCRIPTOR_NOT_VALID ("42202", "Descriptor document is not valid"),
    SC422_03_DESCRIPTOR_NOT_COMPLIANT ("42203", "Descriptor document is not compliant"),
    
    SC422_05_PRODUCT_NOT_VALID ("42205", "Data product is not valid"),
    SC422_04_PRODUCT_ALREADY_EXISTS ("42204", "Data product already exists"),
   
    SC422_06_VERSION_ALREADY_EXISTS ("42206", "Version already exists"),
   
    SC422_08_API_NOT_VALID ("42208", "Api is not valid"),
    SC422_07_API_ALREADY_EXISTS ("42207", "Api already exists"),
    
    SC422_10_SCHEMA_NOT_VALID ("42210", "Schema is not valid"),
    SC422_11_SCHEMA_ALREADY_EXISTS ("42211", "Schema already exists"),
    SC422_12_SCHEMA_TO_API_REL_ALREADY_EXISTS ("42212", "Schema to api relationship alredy exists"),
    
    SC422_13_TEMPLATE_ALREADY_EXISTS ("42213", "Template already exists"),
    SC422_14_TEMPLATE_NOT_VALID ("42214", "Template document is not valid"),

    // Conflict Exceptions
    SC409_01_CONCURRENT_DEPLOYMENT ("40901", "Concurrent deployment task");
    
    private final String code;   
    private final String description; 
    
    RegistryApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}