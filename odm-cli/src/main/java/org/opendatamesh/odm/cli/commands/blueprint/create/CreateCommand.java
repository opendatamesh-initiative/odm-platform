package org.opendatamesh.odm.cli.commands.blueprint.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.odm.cli.commands.blueprint.BlueprintCommands;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(
        name = "create",
        description = "Create a blueprint",
        version = "odm-cli blueprint create 1.0.0",
        mixinStandardHelpOptions = true
)
public class CreateCommand implements Runnable {

    @ParentCommand
    BlueprintCommands blueprintCommands;

    @Option(
            names = "--blueprint-file",
            required = true,
            description = "Path of the blueprint file"
    )
    private String blueprintPath;

    @Option(
            names = "--check",
            description = "Whether to check or not the content of the repository (boolean)"
    )
    private Boolean checkContent;

    @Override
    public void run() {

        BlueprintResource blueprintResource;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String blueprint = FileReaderUtils.readFileFromPath(blueprintPath);
            blueprintResource = objectMapper.readValue(blueprint, BlueprintResource.class);
        } catch (IOException e) {
            System.out.println(
                    "Impossible to parse file [" + blueprintPath + "] as a BlueprintResource."
                            + "Check that the file exists and it's well-formatted."
            );
            return;
        }

        try {

            ResponseEntity<Object> blueprintResponseEntity =
                    blueprintCommands.getBlueprintClient().createBlueprint(blueprintResource, checkContent);

            if (blueprintResponseEntity.getStatusCode().equals(HttpStatus.CREATED))
                System.out.println("Blueprint correctly created: \n" + blueprintResponseEntity.getBody().toString());
            else {
                ErrorRes error = (ErrorRes) blueprintResponseEntity.getBody();
                System.out.println(
                        "Got an unexpected response. Error code  [" + error.getCode() + "]. "
                                + "Error message: " + error.getMessage()
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ResourceAccessException e){
            System.out.println("Impossible to connect with blueprint server. Verify the URL and retry");
        }
    }

}
