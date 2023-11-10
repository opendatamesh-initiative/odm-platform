package org.opendatamesh.odm.cli.commands;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.platform.pp.blueprint.api.clients.BlueprintClient;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
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

    //@Option(names = "--to", split = ",")
    //subsubcommand
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

        String serverUrl = getServerUrl(properties);
        if (serverUrl == null)
            return;

        blueprintClient = new BlueprintClient(serverUrl);

        try {
            ResponseEntity<BlueprintResource[]> blueprintResponseEntity = blueprintClient.readBlueprints();
            BlueprintResource[] blueprintList = blueprintResponseEntity.getBody();
            for(BlueprintResource blueprintResource: blueprintList){
                System.out.println(blueprintResource.getName());
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
    public void initBlueprint(@Option(names = "--id", required = true) Long id, @Option(names = "--instance-file", required = true) String blueprintFilepath) {
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = getServerUrl(properties);
        if (serverUrl == null)
            return;

        String configString = null;
        try {
            configString = FileReaders.readFileFromPath(blueprintFilepath);
        } catch (IOException e) {
            System.out.println("Blueprint file not found");
            return;
        }

        blueprintClient = new BlueprintClient(serverUrl);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ConfigResource configResource = objectMapper.readValue(configString, ConfigResource.class);
            ResponseEntity<Void> blueprintResponseEntity = blueprintClient.instanceBlueprint(id, configResource);
        } catch (JsonProcessingException e) {
            System.out.println("File " + blueprintFilepath + " isn't in the right format");
        } catch (ResourceAccessException e){
            System.out.println("Impossible to connect with blueprint server. Verify the URL and retry");
        }
    }





    /* Checks if the user inserted the specific server URL option. If it is not null than it is returned such value,
    otherwise the properties are checked. If even the property file hasn't the server URL, then the method returns null
     */
    public String getServerUrl(Properties properties){
        if(serverUrlOption == null){
            if(properties == null || properties.getProperty("blueprint-server") == null) {
                System.out.println("The blueprint server URL wasn't specified. Use the -s option or create a file with the \"blueprint-server\" property");
                return null;
            }
            return properties.getProperty("blueprint-server");
        }
        else
            return serverUrlOption;
    }



    @Override
    public void run() {
        System.out.println("Allows to communicate with blueprint module");
    }

}

