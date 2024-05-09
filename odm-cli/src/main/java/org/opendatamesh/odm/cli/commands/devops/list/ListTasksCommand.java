package org.opendatamesh.odm.cli.commands.devops.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "task",
        description = "Lists all the Activity Tasks",
        version = "odm-cli task list 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListTasksCommand implements Runnable {

    @ParentCommand
    DevOpsListCommands devOpsListCommands;

    @Override
    public void run() {
        try {
            final ResponseEntity<ActivityTaskResource[]> tasksResponse = devOpsListCommands.devOpsCommands.getDevOpsClient().getTasks();
            final ActivityTaskResource[] taskResources = tasksResponse.getBody();
            for (ActivityTaskResource activityTaskResource : taskResources) {
                System.out.println(ObjectMapperUtils.formatAsString(activityTaskResource));
            }
        } catch (
                JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (
                ResourceAccessException e) {
            System.out.println("Impossible to connect with activity task server. Verify the URL and retry");
        }
    }
}
