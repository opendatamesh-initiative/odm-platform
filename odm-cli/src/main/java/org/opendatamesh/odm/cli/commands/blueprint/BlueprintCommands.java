package org.opendatamesh.odm.cli.commands.blueprint;

import org.opendatamesh.odm.cli.commands.blueprint.create.CreateCommand;
import org.opendatamesh.odm.cli.commands.blueprint.init.InitCommand;
import org.opendatamesh.odm.cli.commands.blueprint.list.ListCommand;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.odm.cli.utils.InputManagerUtils;
import org.opendatamesh.platform.pp.blueprint.api.clients.BlueprintClient;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.Properties;


@Command(
        name = "blueprint",
        description = "allows to communicate with blueprint module",
        version = "odm-cli blueprint 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                CreateCommand.class,
                InitCommand.class,
                ListCommand.class
        }
)
public class BlueprintCommands implements Runnable {

    BlueprintClient blueprintClient;

    @Option(
            names = { "-s", "--server" },
            description = "URL of the Blueprint server. It must include the port. It overrides the value inside the properties file, if it is present"
    )
    String serverUrlOption;

    @Option(
            names = { "-f", "--properties-file" },
            description = "Path to the properties file",
            defaultValue = "./properties.yml"
    )
    String propertiesFileOption;

    public BlueprintClient getBlueprintClient() {
        if (blueprintClient == null) {
            blueprintClient = setUpBlueprintClient();
        }
        return blueprintClient;
    }

    private BlueprintClient setUpBlueprintClient(){
        Properties properties = null;
        try {
            properties = FileReaderUtils.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = InputManagerUtils.getPropertyValue(properties, "blueprint-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The blueprint server URL wasn't specified. Use the -s option or create a file with the \"blueprint-server\" property");
            throw new RuntimeException("The registry server URL wasn't specified");
        }

        return new BlueprintClient(serverUrl);
    }

    public static void main(String[] args) { CommandLine.run(new BlueprintCommands(), args); }

    @Override
    public void run() { }

}

