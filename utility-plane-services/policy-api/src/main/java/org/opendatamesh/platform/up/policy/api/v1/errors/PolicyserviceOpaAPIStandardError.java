package org.opendatamesh.platform.up.policy.api.v1.errors;

public enum PolicyserviceOpaAPIStandardError {

    SC400_POLICY_ALREADY_EXISTS ("40001", "Policy already exists"),
    SC400_ID_IS_MISSING ("40002", "Policy with missing ID"),
    SC400_ID_IS_EMPTY ("40003", "ID cannot be empty"),
    SC400_IDS_NOT_MATCHING ("40004", "ID conflict"),
    SC400_SUITE_ALREADY_EXISTS ("40005", "Suite already exists"),
    SC400_OPA_SERVER_BAD_REQUEST ("40006", "OPA Bad Request error."),

    SC422_POLICY_SYNTAX_IS_INVALID ("42201", "Policy syntax is invalid"),

    SC404_POLICY_NOT_FOUND ("40401", "Policy not found"),
    SC404_SUITE_NOT_FOUND ("40402", "Suite not found"),

    SC500_OPA_SERVER_INTERNAL_SERVER_ERROR ("50001", "OPA Internal Server Error"),
    SC500_OPA_SERVER_NOT_REACHABLE ("50002", "OPA Server not reachable"),

    SC000_TBD ("00000", "TBD");
    
    private final String code;   
    private final String description; 
    
    PolicyserviceOpaAPIStandardError(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}