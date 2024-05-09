package org.opendatamesh.odm.cli.commands.devops.stop;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "stop",
        description = "Commands to stop a task",
        version = "odm-cli devops start task 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                StopTaskCommand.class
        }
)
public class DevOpsStopActivityCommands implements Runnable {


    @ParentCommand
    protected DevOpsCommands devOpsCommands;

    @Override
    public void run() {
    }

}

