package org.opendatamesh.odm.cli.commands.devops;


import org.opendatamesh.odm.cli.commands.devops.get.DevOpsGetCommands;
import org.opendatamesh.odm.cli.commands.devops.list.DevOpsListCommands;
import org.opendatamesh.odm.cli.commands.devops.publish.DevOpsPublishCommands;
import org.opendatamesh.odm.cli.commands.devops.get.GetTaskCommand;
import org.opendatamesh.odm.cli.commands.devops.list.ListActivitiesCommand;
import org.opendatamesh.odm.cli.commands.devops.list.ListTasksCommand;
import org.opendatamesh.odm.cli.commands.devops.start.DevOpsStartActivityCommand;
import org.opendatamesh.odm.cli.commands.devops.start.StartActivityCommand;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.odm.cli.utils.InputManagerUtils;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.Properties;

@Command(
        name = "devops",
        description = "allows to communicate with devops module",
        version = "odm-cli devops 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                DevOpsPublishCommands.class,
                DevOpsGetCommands.class,
                DevOpsListCommands.class
                        }
)
public class DevOpsCommands implements Runnable {

    DevOpsClient devOpsClient;

    @Option(
            names = {"-s", "--server"},
            description = "URL of the DevOps server. It must include the port. It overrides the value inside the properties file, if it is present"
    )
    String serverUrlOption;

    @Option(
            names = {"-f", "--properties-file"},
            description = "Path to the properties file",
            defaultValue = "./properties.yml"
    )
    String propertiesFileOption;

    public DevOpsClient getDevOpsClient() {
        if (devOpsClient == null) {
            devOpsClient = setUpDevOpsClient();
        }
        return devOpsClient;
    }

    private DevOpsClient setUpDevOpsClient() {
        Properties properties = null;
        try {
            properties = FileReaderUtils.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }
        String serverUrl = InputManagerUtils.getPropertyValue(properties, "devops-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The devOps server URL wasn't specified. Use the -s option or create a file with the \"devops-server\" property");
            throw new RuntimeException("The registry server URL wasn't specified");
        }

        return new DevOpsClient(serverUrl);
    }

    public static void main(String[] args) {
        CommandLine.run(new DevOpsCommands(), args);
    }

    @Override
    public void run() {
    }
}
