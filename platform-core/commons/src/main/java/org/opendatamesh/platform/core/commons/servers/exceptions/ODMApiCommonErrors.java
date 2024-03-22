package org.opendatamesh.platform.core.commons.servers.exceptions;

public enum ODMApiCommonErrors implements ODMApiStandardErrors {

    // Bad Request Exceptions
    SC400_00_REQUEST_BODY_IS_NOT_READABLE ("40000", "Request body is not readable"),
    

    // Not Acceptable Media Type Exception
    SC406_01_REQUEST_ACCEPTED_MEDIA_TYPES_NOT_SUPPORTED ("40601", "Request accepted media types not supported"),

    // Unsupported Media Type Exception
    SC415_01_REQUEST_MEDIA_TYPE_NOT_SUPPORTED ("41501", "Request media type not supported"),

    // Internal Server Exceptions
    SC500_00_SERVICE_ERROR ("50000", "Error in the backend service"),
    SC500_01_DATABASE_ERROR ("50001", "Error in the backend database"),
    SC500_02_DESCRIPTOR_ERROR ("50002", "Error in the backend descriptor processor"),
    SC500_50_REGISTRY_SERVICE_ERROR ("50050", "Registry service is disabled or not reachable"),
    SC500_70_NOTIFICATION_SERVICE_ERROR ("50070", "Notification service is disabled or not reachable"),
    SC500_71_POLICY_SERVICE_ERROR ("50071", "Policy service is disabled or not reachable"),
    SC500_73_POLICY_SERVICE_EVALUATION_ERROR ("50073", "Policy service failed evaluations"),
    SC500_72_EXECUTOR_SERVICE_ERROR ("50072", "Executor service is disabled or not reachable"),

    // Bad Gateway Exceptions
    SC502_50_REGISTRY_SERVICE_ERROR ("50250", "Registry service returns an error"),
    SC502_70_NOTIFICATION_SERVICE_ERROR ("50270", "Notification service returns an error"),
    SC502_71_POLICY_SERVICE_ERROR ("50271", "Policy service returns an error"),
    SC502_72_EXECUTOR_SERVICE_ERROR ("50272", "Executor service returns an error");

    //SC000_TBD ("00000", "TBD");
    
    private final String code;   
    private final String description; 

    public static final String SC400_00_CODE = "40000", SC400_00_DESC = "Activity not found";


    ODMApiCommonErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}