package org.opendatamesh.odm.cli.commands.devops.get;

import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "activity",
        description = "Get a specific Activity",
        version = "odm-cli devops get activity 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetActivityCommand implements Runnable {

    @ParentCommand
    private DevOpsGetCommands devopsGetCommands;

    @Option(
            names = "--id",
            description = "ID of the activity",
            required = true
    )
    Long activityId;

    @Override
    public void run() {
        try {

            final ResponseEntity<ActivityResource> activityResponseEntity = devopsGetCommands.devOpsCommands.getDevOpsClient().getActivity(activityId);
            if (activityResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                final ActivityResource activityResource = activityResponseEntity.getBody();
                System.out.println(ObjectMapperUtils.formatAsString(activityResource));
            } else if (activityResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Activity with ID [" + activityId + "] not found");
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + activityResponseEntity.getStatusCode()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
