package org.opendatamesh.odm.cli.commands.registry;

import org.opendatamesh.odm.cli.commands.registry.get.GetCommand;
import org.opendatamesh.odm.cli.commands.registry.list.ListCommand;
import org.opendatamesh.odm.cli.commands.registry.publish.PublishCommand;
import org.opendatamesh.odm.cli.commands.registry.upload.UploadCommand;
import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.odm.cli.utils.InputManager;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.Properties;


@Command(
        name = "registry",
        description = "allows to communicate with registry module",
        version = "odm-cli registry 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                ListCommand.class,
                GetCommand.class,
                PublishCommand.class,
                UploadCommand.class
        }
)
public class RegistryCommands implements Runnable {

    RegistryClient registryClient;

    @Option(
            names = { "-s", "--server" },
            description = "URL of the Registry server. It must include the port. It overrides the value inside the properties file, if it is present"
    )
    String serverUrlOption;

    @Option(
            names = { "-f", "--properties-file" },
            description = "Path to the properties file",
            defaultValue = "./properties.yml"
    )
    String propertiesFileOption;

    public RegistryClient getRegistryClient() {
        if (registryClient == null) {
            registryClient = setUpRegistryClient();
        }
        return registryClient;
    }

    private RegistryClient setUpRegistryClient(){
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = InputManager.getPropertyValue(properties, "registry-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The registry server URL wasn't specified. Use the -s option or create a file with the \"registry-server\" property");
            throw new RuntimeException("The registry server URL wasn't specified");
        }

        return new RegistryClient(serverUrl);
    }

    public static void main(String[] args) {
        CommandLine.run(new RegistryCommands(), args);
    }

    @Override
    public void run() {}

}