package org.opendatamesh.odm.cli.commands.devops.list;


import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "list",
        description = "Commands to list objects related to DevOps microservice",
        mixinStandardHelpOptions = true,
        version = "odm-cli devops list 1.0.0",
        subcommands = {
                ListActivitiesCommand.class,
                ListTasksCommand.class
        }
)
public class DevOpsListCommands implements Runnable {

    @ParentCommand
    protected DevOpsCommands devOpsCommands;

    @Override
    public void run() {
    }

}

