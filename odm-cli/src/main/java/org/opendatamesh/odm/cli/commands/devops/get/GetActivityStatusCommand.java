package org.opendatamesh.odm.cli.commands.devops.get;


import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatusResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "activity-status",
        description = "Get the status a specific Activity",
        version = "odm-cli devops get activity-status 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetActivityStatusCommand implements Runnable {

    @ParentCommand
    private DevOpsCommands devOpsCommands;

    @Option(
            names = "--id",
            description = "ID of the activity",
            required = true
    )
    Long activityId;

    @Override
    public void run() {
        try {

            final ResponseEntity<ActivityStatusResource> activityStatus = devOpsCommands.getDevOpsClient().getActivityStatus(activityId);
            if (activityStatus.getStatusCode().equals(HttpStatus.OK)) {
                final ActivityStatusResource activityStatusResourceResource = activityStatus.getBody();
                System.out.println(ObjectMapperUtils.formatAsString(activityStatusResourceResource));
            } else if (activityStatus.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Activity status of activity number: [" + activityId + "] not found");
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + activityStatus.getStatusCode()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
