
package org.opendatamesh.platform.pp.devops.api.resources;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum DevOpsApiStandardErrors implements ODMApiStandardErrors {

    // Bad Request Exceptions
    SC400_50_ACTIVITY_ID_IS_EMPTY ("40050", "Activity id is empty"),
    SC400_55_ACTIVITY_STATUS_ACTION_IS_INVALID ("40055", "Activity status action is invalid"),
    SC400_60_TASK_ID_IS_EMPTY ("40060", "Task id is empty"),
    SC400_65_TASK_STATUS_ACTION_IS_INVALID ("40065", "Task status action is invalid"),
    
    
    // Not Found Exceptions
    SC404_01_ACTIVITY_NOT_FOUND ("40401", "Activity not found"),
    SC404_11_TASK_NOT_FOUND ("40411", "Task not found"),

    // Conflict Exceptions
    SC409_01_CONCURRENT_ACTIVITIES ("40901", "There is already a running activity on given product version"),

    // Unprocessable Entity Exceptions
    SC422_01_ACTIVITY_IS_INVALID ("42201", "Activity is invalid"),
    SC422_02_ACTIVITY_ALREADY_EXISTS ("42202", "Activity already exists"),
    SC422_03_TASK_RESULT_IS_INVALID("42203", "Task Result resource is invalid"),;

    
    private final String code;   
    private final String description; 

    public static final String SC400_00_CODE = "40000", SC400_00_DESC = "Activity not found";


    DevOpsApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}