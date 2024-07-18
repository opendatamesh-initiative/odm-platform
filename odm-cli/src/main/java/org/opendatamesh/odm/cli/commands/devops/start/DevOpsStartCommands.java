package org.opendatamesh.odm.cli.commands.devops.start;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "start",
        description = "Commands to start the execution of objects stored in the DevOps microservice",
        version = "odm-cli devops start 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                StartActivityCommand.class
        }
)
public class DevOpsStartCommands implements Runnable {


    @ParentCommand
    protected DevOpsCommands devOpsCommands;

    @Override
    public void run() {
    }

}

