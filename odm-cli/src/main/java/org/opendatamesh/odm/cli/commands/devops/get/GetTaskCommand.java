package org.opendatamesh.odm.cli.commands.devops.get;

import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "task",
        description = "Get a specific Task given its ID",
        version = "odm-cli devops get task 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetTaskCommand implements Runnable {

    @ParentCommand
    private DevOpsGetCommands devOpsGetCommands;

    @Option(
            names = "--id",
            description = "ID of the task",
            required = true
    )
    Long taskId;

    @Override
    public void run() {
        try {
            ResponseEntity<ActivityTaskResource> taskResponseEntity = devOpsGetCommands.devOpsCommands.getDevOpsClient().getTask(taskId);
            if (taskResponseEntity.getStatusCode().equals(HttpStatus.OK))
                System.out.println(ObjectMapperUtils.formatAsString(taskResponseEntity.getBody()));
            else if (taskResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Task number: [" + taskId + "] not found");
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + taskResponseEntity.getStatusCode()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
