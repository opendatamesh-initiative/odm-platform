package org.opendatamesh.odm.cli.commands.devops.start;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatusResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;


@Command(
        name = "activity",
        description = "Start a specific Activity",
        version = "odm-cli devops start activity 1.0.0",
        mixinStandardHelpOptions = true
)
public class StartActivityCommand implements Runnable {

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
            final ResponseEntity<ActivityStatusResource> activityStatusResourceResponseEntity = devOpsCommands.getDevOpsClient().patchActivityStart(activityId);
            if (activityStatusResourceResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                final ActivityStatusResource activityStatusResourceResource = activityStatusResourceResponseEntity.getBody();
                System.out.println(ObjectMapperUtils.formatAsString(activityStatusResourceResource));
            } else if (activityStatusResourceResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Activity number: [" + activityId + "] not found");
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + activityStatusResourceResponseEntity.getStatusCode()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
