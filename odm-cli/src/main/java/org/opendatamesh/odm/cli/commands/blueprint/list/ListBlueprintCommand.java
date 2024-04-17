package org.opendatamesh.odm.cli.commands.blueprint.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.commands.blueprint.BlueprintCommands;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "list",
        description = "Lists all the blueprints",
        version = "odm-cli blueprint list 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListBlueprintCommand implements Runnable {

    @ParentCommand
    BlueprintCommands blueprintCommands;

    @Override
    public void run() {
        try {
            ResponseEntity<BlueprintResource[]> blueprintResponseEntity =
                    blueprintCommands.getBlueprintClient().readBlueprints();
            BlueprintResource[] blueprintList = blueprintResponseEntity.getBody();
            for(BlueprintResource blueprintResource: blueprintList){
                System.out.println(ObjectMapperUtils.formatAsString(blueprintResource));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ResourceAccessException e){
            System.out.println("Impossible to connect with blueprint server. Verify the URL and retry");
        }
    }

}