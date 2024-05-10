package org.opendatamesh.odm.cli.commands.devops.stop;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "stop",
        description = "Commands to stop the execution of objects stored in the DevOps microservice",
        version = "odm-cli devops stop 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                StopTaskCommand.class
        }
)
public class DevOpsStopCommands implements Runnable {


    @ParentCommand
    protected DevOpsCommands devOpsCommands;

    @Override
    public void run() {
    }

}

