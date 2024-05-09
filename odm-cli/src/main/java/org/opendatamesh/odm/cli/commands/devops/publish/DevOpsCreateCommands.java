package org.opendatamesh.odm.cli.commands.devops.publish;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import picocli.CommandLine;
import picocli.CommandLine.Command;

public class DevOpsCreateCommands implements Runnable {

    @Command(
            name = "publish",
            description = "Commands to publish activities",
            version = "odm-cli devops publish 1.0.0",
            mixinStandardHelpOptions = true,
            subcommands = {
                    PublishActivityCommand.class
            }
    )

    @CommandLine.ParentCommand
    protected DevOpsCommands devOpsCommands;

    @Override
    public void run() {
    }

}


