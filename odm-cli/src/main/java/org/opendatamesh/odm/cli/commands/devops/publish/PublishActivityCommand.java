package org.opendatamesh.odm.cli.commands.devops.publish;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(
        name = "activity",
        description = "Create an activity",
        version = "odm-cli devops publish activity 1.0.0",
        mixinStandardHelpOptions = true
)
public class PublishActivityCommand implements Runnable {

    @ParentCommand
    DevOpsCreateCommands devopsCommands;

    @Option(
            names = "--activity-file",
            required = true,
            description = "Path of the activity file"
    )
    private String activityPath;

    @Option(
            names = "--check",
            description = "Whether to check or not the content of the repository (boolean)"
    )
    private Boolean checkContent;

    @Override
    public void run() {
        ActivityResource activityResource;
        try {
            activityResource = ObjectMapperUtils.stringToResource(
                    FileReaderUtils.readFileFromPath(activityPath),
                    ActivityResource.class
            );
        } catch (IOException e) {
            System.out.println(
                    "Impossible to parse file [" + activityPath + "] as a ActivityResource."
                            + "Check that the file exists and it's well-formatted."
            );
            return;
        }
        try {
            ResponseEntity<ActivityResource> activityResponseEntity =
                    devopsCommands.devOpsCommands.getDevOpsClient().postActivity(activityResource, checkContent);
            if (activityResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                activityResource = ObjectMapperUtils.convertObject(activityResponseEntity.getBody(), ActivityResource.class);
                System.out.println("Activity correctly created: \n" + ObjectMapperUtils.formatAsString(activityResource));
            } else {
                ErrorRes error = ObjectMapperUtils.convertObject(activityResponseEntity.getBody(), ErrorRes.class);
                System.out.println(
                        "Got an unexpected response. Error code  [" + error.getCode() + "]. "
                                + "Error message: " + error.getMessage()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with blueprint server. Verify the URL and retry");
        }
    }
}
