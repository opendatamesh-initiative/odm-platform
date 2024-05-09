package org.opendatamesh.odm.cli.commands.devops.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "list",
        description = "Lists all the Activities",
        version = "odm-cli activities list 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListActivitiesCommand implements Runnable {

    @ParentCommand
    DevOpsCommands devOpsCommands;

    @Override
    public void run() {
        try {
            final ResponseEntity<ActivityResource[]> activities = devOpsCommands.getDevOpsClient().getActivities();
            final ActivityResource[] activityResources = activities.getBody();
            for (ActivityResource blueprintResource : activityResources) {
                System.out.println(ObjectMapperUtils.formatAsString(blueprintResource));
            }
        } catch (
                JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (
                ResourceAccessException e) {
            System.out.println("Impossible to connect with activity server. Verify the URL and retry");
        }
    }
}
