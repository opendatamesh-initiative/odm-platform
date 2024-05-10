package org.opendatamesh.odm.cli.commands.devops.stop;


import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.TaskStatusResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "task",
        description = "Stop a specific Activity",
        version = "odm-cli devops stop task 1.0.0",
        mixinStandardHelpOptions = true
)
public class StopTaskCommand implements Runnable {

    @ParentCommand
    private DevOpsStopActivityCommands devOpsStopActivityCommand;

    @Option(
            names = "--id",
            description = "ID of the task",
            required = true
    )
    Long taskId;

    @Override
    public void run() {
        try {
            final ResponseEntity<TaskStatusResource> taskStatusResourceResponseEntity = devOpsStopActivityCommand.devOpsCommands.getDevOpsClient().patchTaskStop(taskId);
            if (taskStatusResourceResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                final TaskStatusResource taskStatusResourceResponseEntityBody = taskStatusResourceResponseEntity.getBody();
                System.out.println(ObjectMapperUtils.formatAsString(taskStatusResourceResponseEntityBody));
            } else if (taskStatusResourceResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Task number: [" + taskId + "] not found");
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + taskStatusResourceResponseEntity.getStatusCode()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
