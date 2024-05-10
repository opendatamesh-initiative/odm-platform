package org.opendatamesh.odm.cli.commands.devops.get;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatusResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "activity",
        description = "Get a specific Activity given its ID",
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

    @Option(
            names = "--status",
            description = "Whether to retrieve only the status of the Activity (true) or the full Activity (false)",
            required = true,
            defaultValue = "false"
    )
    Boolean status;

    @Override
    public void run() {
        try {
            if(status != null && status) {
                // Retrieve only status
                getActivityStatus(activityId);
            } else {
                // Retrieve full activity
                getActivityResource(activityId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void getActivityResource(Long activityId) throws JsonProcessingException {
        ResponseEntity<ActivityResource> activityResponseEntity = devopsGetCommands.devOpsCommands.getDevOpsClient().getActivity(activityId);
        if (activityResponseEntity.getStatusCode().equals(HttpStatus.OK))
            System.out.println(ObjectMapperUtils.formatAsString(activityResponseEntity.getBody()));
        else if (activityResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
            System.out.println("Activity with ID [" + activityId + "] not found");
        else
            logError(activityResponseEntity.getStatusCode());
    }

    private void getActivityStatus(Long activityId) throws JsonProcessingException {
        ResponseEntity<ActivityStatusResource> activityStatusResponseEntity = devopsGetCommands.devOpsCommands.getDevOpsClient().getActivityStatus(activityId);
        if (activityStatusResponseEntity.getStatusCode().equals(HttpStatus.OK))
            System.out.println(ObjectMapperUtils.formatAsString(activityStatusResponseEntity.getBody()));
        else if (activityStatusResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
            System.out.println("Activity status of activity number: [" + activityId + "] not found");
        else
            logError(activityStatusResponseEntity.getStatusCode());
    }

    private void logError(HttpStatus statusCode) {
        System.out.println("Got an unexpected response. Error code: " + statusCode);
    }

}
