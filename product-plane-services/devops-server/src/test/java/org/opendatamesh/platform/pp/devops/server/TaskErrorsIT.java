package org.opendatamesh.platform.pp.devops.server;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class TaskErrorsIT extends ODMDevOpsIT {

    // ======================================================================================
    // CREATE Activity
    // ======================================================================================

    // Note: Task cannot be created directly.
    // Tasks are created when the parent activity is created

    // ======================================================================================
    // START/STOP Task
    // ======================================================================================

    // Note: A task can be stopped but not directly started.
    // Tasks are started when the parent activity is started
    

    // ======================================================================================
    // READ Task's status
    // ======================================================================================
    
  
    // ======================================================================================
    // READ task
    // ======================================================================================
    

    // ======================================================================================
    // SEARCH task
    // ======================================================================================
    
   
}