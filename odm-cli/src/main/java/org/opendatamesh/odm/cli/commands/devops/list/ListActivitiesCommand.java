package org.opendatamesh.odm.cli.commands.devops.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "activity",
        description = "Lists all the Activities",
        version = "odm-cli list activity 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListActivitiesCommand implements Runnable {

    @ParentCommand
    DevOpsListCommands devOpsListCommands;

    @Override
    public void run() {
        try {
            ResponseEntity<ActivityResource[]> activityResponseEntitity =
                    devOpsListCommands.devOpsCommands.getDevOpsClient().getActivities();
            if (activityResponseEntitity.getStatusCode().equals(HttpStatus.OK)) {
                ActivityResource[] activities = activityResponseEntitity.getBody();
                if (activities.length == 0)
                    System.out.println("[]");
                for (ActivityResource activityResource : activities)
                    System.out.println(ObjectMapperUtils.formatAsString(activityResource));
            } else
                System.out.println("Error in response from DevOps Server");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with devops server. Verify the URL and retry");
        }
    }

}
