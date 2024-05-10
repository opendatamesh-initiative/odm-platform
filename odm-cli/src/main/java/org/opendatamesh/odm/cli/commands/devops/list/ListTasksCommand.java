package org.opendatamesh.odm.cli.commands.devops.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "task",
        description = "Lists all the Activity Tasks",
        version = "odm-cli list task 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListTasksCommand implements Runnable {

    @ParentCommand
    DevOpsListCommands devOpsListCommands;

    @Override
    public void run() {
        try {
            ResponseEntity<ActivityTaskResource[]> tasksResponse =
                    devOpsListCommands.devOpsCommands.getDevOpsClient().getTasks();
            if (tasksResponse.getStatusCode().equals(HttpStatus.OK)) {
                ActivityTaskResource[] taskResources = tasksResponse.getBody();
                if (taskResources.length == 0)
                    System.out.println("[]");
                for (ActivityTaskResource activityTaskResource : taskResources)
                    System.out.println(ObjectMapperUtils.formatAsString(activityTaskResource));
            } else
                System.out.println("Error in response from DevOps Server");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with devops server. Verify the URL and retry");
        }
    }

}
