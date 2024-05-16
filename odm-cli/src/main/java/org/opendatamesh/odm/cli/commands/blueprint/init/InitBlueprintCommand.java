package org.opendatamesh.odm.cli.commands.blueprint.init;

import com.fasterxml.jackson.core.type.TypeReference;
import org.opendatamesh.odm.cli.commands.blueprint.BlueprintCommands;
import org.opendatamesh.odm.cli.utils.InputManagerUtils;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command(
        name = "init",
        description = "Initialize a project given the blueprint and a set of parameters",
        version = "odm-cli blueprint init 1.0.0",
        mixinStandardHelpOptions = true
)
public class InitBlueprintCommand implements Runnable {

    @ParentCommand
    BlueprintCommands blueprintCommands;

    @Option(
            names = "--id",
            required = true,
            description = "ID of the blueprint to initialize"
    )
    private Long blueprintId;

    @Override
    public void run() {
        ResponseEntity<BlueprintResource> blueprintResourceResponseEntity;
        try {
            blueprintResourceResponseEntity = blueprintCommands.getBlueprintClient().readOneBlueprint(blueprintId);
            if (blueprintResourceResponseEntity.getStatusCode().equals(HttpStatus.OK)){
                BlueprintResource blueprintResource = blueprintResourceResponseEntity.getBody();
                List<Map<String,String>> blueprintParams = ObjectMapperUtils.convertString(
                        blueprintResource.getBlueprintParams(), new TypeReference<>() {}
                );
                ConfigResource configResource = new ConfigResource();
                String targetRepo = InputManagerUtils.getValueFromUser("Insert target repo: ");
                configResource.setTargetRepo(targetRepo);
                String createRepo = InputManagerUtils.getValueFromUser("Create repo [T/F]: ");
                switch (createRepo){
                    case "T":
                        configResource.setCreateRepo(true);
                        break;
                    case "F":
                        configResource.setCreateRepo(false);
                        break;
                    default:
                        System.out.println("Invalid input");
                        return;
                }
                Map<String, String> configMap = new HashMap<>();
                for(Map<String,String> param : blueprintParams) {
                    String message = "Insert a value (blank for default) for param: \n{ \n\tName: "
                            + param.get("name") + ", \n\tDescription: " + param.get("description") +
                            ", \n\tDefault value: " + param.get("defaultValue")  +  "\n}";
                    String paramInput = InputManagerUtils.getValueFromUser(message, param.get("defaultValue"));
                    configMap.put(param.get("name"), paramInput);
                }
                configResource.setConfig(configMap);
                ResponseEntity<Void> blueprintResponseEntity =
                        blueprintCommands.getBlueprintClient().instanceBlueprint(blueprintId, configResource);
                if(blueprintResponseEntity.getStatusCode().equals(HttpStatus.OK))
                    System.out.println("Blueprint instanced correctly");
                else
                    System.out.println("Something went wrong when communicating with Blueprint Server");
            }
            else
                System.out.println("Something went wrong when communicating with Blueprint Server");
        } catch (ResourceAccessException e){
            System.out.println("Impossible to connect with blueprint server. Verify the URL and retry");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}