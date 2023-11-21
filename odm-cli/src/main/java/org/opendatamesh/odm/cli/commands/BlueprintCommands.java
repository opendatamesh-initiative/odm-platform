package org.opendatamesh.odm.cli.commands;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.odm.cli.utils.PropertiesManager;
import org.opendatamesh.platform.pp.blueprint.api.clients.BlueprintClient;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


@Command(
        name = "blueprint",
        description = "allows to communicate with blueprint module",
        version = "odm-cli blueprint 1.0.0",
        mixinStandardHelpOptions = true
)
public class BlueprintCommands implements Runnable {

    BlueprintClient blueprintClient;

    @Option(names = {"-s", "--server"},
            description = "URL of the Blueprint server. It must include the port. It overrides the value inside the properties file, if it is present",
            required = false)
    String serverUrlOption;

    @Option(names = {"-f", "--properties-file"},
            description = "Path to the properties file",
            defaultValue = "./properties.yml",
            required = false)
    String propertiesFileOption;

    public static void main(String[] args) {CommandLine.run(new BlueprintCommands(), args);
    }

    @Command(name = "list",
            description = "lists all the blueprints",
            version = "odm-cli blueprint list 1.0.0",
            mixinStandardHelpOptions = true)
    public void listBlueprints() {
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = PropertiesManager.getPropertyValue(properties, "blueprint-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The blueprint server URL wasn't specified. Use the -s option or create a file with the \"blueprint-server\" property");
            return;
        }

        blueprintClient = new BlueprintClient(serverUrl);

        try {
            ResponseEntity<BlueprintResource[]> blueprintResponseEntity = blueprintClient.readBlueprints();
            BlueprintResource[] blueprintList = blueprintResponseEntity.getBody();
            for(BlueprintResource blueprintResource: blueprintList){
                System.out.println(blueprintResource);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ResourceAccessException e){
            System.out.println("Impossible to connect with blueprint server. Verify the URL and retry");
        }
    }

    @Command(name = "initBlueprint",
            description = "init a blueprint",
            version = "odm-cli blueprint list 1.0.0",
            mixinStandardHelpOptions = true)
    public void initBlueprint(@Option(names = "--id", required = true) Long id) {
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = PropertiesManager.getPropertyValue(properties, "blueprint-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The blueprint server URL wasn't specified. Use the -s option or create a file with the \"blueprint-server\" property");
            return;
        }

        blueprintClient = new BlueprintClient(serverUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<BlueprintResource> blueprintResourceResponseEntity;

        try {
            blueprintResourceResponseEntity = blueprintClient.readOneBlueprint(id);
            if (blueprintResourceResponseEntity.getStatusCode().equals(HttpStatus.OK)){
                BlueprintResource blueprintResource = blueprintResourceResponseEntity.getBody();
                List<Map<String,String>> blueprintParams = objectMapper.readValue(blueprintResource.getBlueprintParams(), new TypeReference<List<Map<String, String>>>(){});

                ConfigResource configResource = new ConfigResource();
                System.out.print("Insert target repo: ");
                String input = System.console().readLine();
                configResource.setTargetRepo(input);
                System.out.print("Create report[T/F]: ");
                input = System.console().readLine();
                switch (input){
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

                for(Map<String,String> param : blueprintParams){
                    System.out.print("You must insert param \"" + param.get("name") + "\". Description: " + param.get("description") +
                            ". Default value: " + param.get("defaultValue")  +  ". \nValue (blank for default): " );
                    String paramInput = System.console().readLine();
                    if(paramInput == null || paramInput.isBlank()) {
                        paramInput = param.get("defaultValue");
                    }

                    configMap.put(param.get("name"), paramInput);

                }
                configResource.setConfig(configMap);
                ResponseEntity<Void> blueprintResponseEntity = blueprintClient.instanceBlueprint(id, configResource);

                if(blueprintResponseEntity.getStatusCode().equals(HttpStatus.OK))
                    System.out.println("Blueprint instanced correctly");
                else
                    System.out.println("Something went wrong when communicating with Blueprint Server");
            }
            else
                System.out.println("Something went wrong when communicating with Blueprint Server");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }catch (ResourceAccessException e){
            System.out.println("Impossible to connect with blueprint server. Verify the URL and retry");
            return;
        }
    }




    @Override
    public void run() {
        System.out.println("Allows to communicate with blueprint module");
    }

}

