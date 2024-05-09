package org.opendatamesh.odm.cli.commands.devops.start;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "start",
        description = "Commands to start activities",
        version = "odm-cli devops start activity 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                StartActivityCommand.class
        }
)
public class DevOpsStartActivityCommand implements Runnable {


    @ParentCommand
    protected DevOpsCommands devOpsCommands;

    @Override
    public void run() {
    }

}

