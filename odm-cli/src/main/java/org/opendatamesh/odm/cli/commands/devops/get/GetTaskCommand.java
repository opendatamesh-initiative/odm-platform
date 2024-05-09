package org.opendatamesh.odm.cli.commands.devops.get;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Command;


@Command(
        name = "task",
        description = "Get a task",
        version = "odm-cli devops get task 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetTaskCommand implements Runnable {

    @ParentCommand
    private DevOpsCommands devOpsCommands;

    @Option(
            names = "--id",
            description = "ID of the task",
            required = true
    )
    Long taskId;

    @Override
    public void run() {
        try {

            final ResponseEntity<ActivityTaskResource> task = devOpsCommands.getDevOpsClient().getTask(taskId);
            if (task.getStatusCode().equals(HttpStatus.OK)) {
                final ActivityTaskResource taskResource = task.getBody();
                System.out.println(ObjectMapperUtils.formatAsString(taskResource));
            } else if (task.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Task number: [" + taskId + "] not found");
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + task.getStatusCode()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
