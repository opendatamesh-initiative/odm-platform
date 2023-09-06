package org.opendatamesh.platform.up.executor.api.resources;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum ExecutorApiStandardErrors implements ODMApiStandardErrors {

    // Bad Request Exceptions
    
    // Not Found Exceptions
    
    // Unprocessable Entity Exceptions
    SC422_05_TASK_IS_INVALID ("42201", "Task is invalid"),
  
    // Conflict Exceptions
    SC409_01_CONCURRENT_EXECUTIONS ("40901", "Task is already started"),

    // Internal Server Exceptions
    SC500_50_REGISTRY_SERVICE_ERROR ("50050", "Azure Devops API or not reachable"),
    

    // Bad Gateway Exceptions
    SC502_50_REGISTRY_SERVICE_ERROR ("50250", "Azure Devops API returns an error");
    
    private final String code;   
    private final String description; 
    
    ExecutorApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}