package org.opendatamesh.odm.cli.commands.devops.get;

import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "get",
        description = "Commands to get objects related to DevOps microservice",
        mixinStandardHelpOptions = true,
        version = "odm-cli devops get 1.0.0",
        subcommands = {
                GetActivityCommand.class,
                GetTaskCommand.class
        }
)
public class DevOpsGetCommands implements Runnable{

    @ParentCommand
    protected DevOpsCommands devOpsCommands;

    @Override
    public void run() { }

}
