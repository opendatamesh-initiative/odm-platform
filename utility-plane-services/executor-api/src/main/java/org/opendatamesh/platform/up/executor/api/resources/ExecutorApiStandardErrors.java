package org.opendatamesh.platform.up.executor.api.resources;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum ExecutorApiStandardErrors implements ODMApiStandardErrors {

    // Bad Request Exceptions
    SC400_01_TASK_ID_IS_EMPTY ("40001", "Task Id wasn't provided"),

    // Unathorized Exceptions
    SC401_01_EXECUTOR_UNATHORIZED("40101", "The executor client id isn't authorized for the request"),

    // Forbidden exception
    SC403_01_EXECUTOR_FORBIDDEN("40301", "The request is forbidden for the executor client id"),

    // Not Found Exceptions
    SC404_01_PIPELINE_RUN_NOT_FOUND ("40401", "Pipeline run not found"),

    // Unprocessable Entity Exceptions
    SC422_05_TASK_IS_INVALID ("42201", "Task is invalid"),
  
    // Conflict Exceptions
    SC409_01_CONCURRENT_EXECUTIONS ("40901", "Task is already started"),

    // Internal Server Exceptions
    SC500_50_EXECUTOR_SERVICE_ERROR ("50050", "Executor server error");
    
    private final String code;   
    private final String description; 
    
    ExecutorApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}