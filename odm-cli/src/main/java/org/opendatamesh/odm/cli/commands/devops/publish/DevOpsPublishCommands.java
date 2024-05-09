package org.opendatamesh.odm.cli.commands.devops.publish;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "publish",
        description = "Commands to publish activities",
        version = "odm-cli devops publish 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                PublishActivityCommand.class
        }
)
public class DevOpsPublishCommands implements Runnable {


    @ParentCommand
    protected DevOpsCommands devOpsCommands;

    @Override
    public void run() {
    }

}


