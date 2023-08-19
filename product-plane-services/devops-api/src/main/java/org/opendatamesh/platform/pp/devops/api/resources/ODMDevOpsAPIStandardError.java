
package org.opendatamesh.platform.pp.devops.api.resources;

public enum ODMDevOpsAPIStandardError {

    // Bad Request Exceptions
    SC400_00_REQUEST_BODY_IS_NOT_READABLE ("40000", "Request body is not readable"),
    SC400_50_ACTIVITY_ID_IS_EMPTY ("40050", "Activity id is empty"),
    SC400_60_TASK_ID_IS_EMPTY ("40060", "Task id is empty"),
    SC400_99_PROPERTY_REF_EXCEPTION ("40099", "Property Reference Exception"),
   
    // Not Found Exceptions
    SC404_01_ACTIVITY_NOT_FOUND ("40401", "Activity not found"),
    SC404_11_TASK_NOT_FOUND ("40411", "Task not found"),


    // Not Acceptable Media Type Exception
    SC406_01_REQUEST_ACCEPTED_MEDIA_TYPES_NOT_SUPPORTED ("40601", "Request accepted media types not supported"),

     // Conflict Exceptions
    SC409_01_CONCURRENT_ACTIVITIES ("40901", "There is already a running activity on given product version"),

    // Unsupported Media Type Exception
    SC415_01_REQUEST_MEDIA_TYPE_NOT_SUPPORTED ("41501", "Request media type not supported"),

    // Unprocessable Entity Exceptions
    SC422_01_ACTIVITY_IS_INVALID ("42201", "Activity is invalid"),
    SC422_02_ACTIVITY_ALREADY_EXISTS ("42202", "Activity already exists"),
    

    // Internal Server Exceptions
    SC500_00_SERVICE_ERROR ("50000", "Error in the backend service"),
    SC500_01_DATABASE_ERROR ("50001", "Error in the backend database"),
    SC500_02_DESCRIPTOR_ERROR ("50002", "Error in the backend descriptor processor"),
    SC500_50_REGISTRY_SERVICE_ERROR ("50050", "Registry service is disabled or not reachable"),
    SC500_70_NOTIFICATION_SERVICE_ERROR ("50070", "Notification service is disabled or not reachable"),
    SC500_71_POLICY_SERVICE_ERROR ("50071", "Policy service is disabled or not reachable"),
    SC500_72_EXECUTOR_SERVICE_ERROR ("50072", "Executor service is disabled or not reachable"),

    // TODO manage error response returned from external services  
    // Bad Gateway Exceptions
    SC502_50_REGISTRY_SERVICE_ERROR ("50250", "Registry service returns an error"),
    SC502_70_NOTIFICATION_SERVICE_ERROR ("50270", "Notification service returns an error"),
    SC502_71_POLICY_SERVICE_ERROR ("50271", "Policy service returns an error"),
    SC502_72_EXECUTOR_SERVICE_ERROR ("50272", "Executor service returns an error"),


    SC000_TBD ("00000", "TBD");
    
    private final String code;   
    private final String description; 

    public static final String SC400_00_CODE = "40000", SC400_00_DESC = "Activity not found";


    ODMDevOpsAPIStandardError(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}