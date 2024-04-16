package org.opendatamesh.odm.cli.commands.registry.get;

import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "get",
        description = "Commands to get data products and versions",
        mixinStandardHelpOptions = true,
        version = "odm-cli registry get 1.0.0",
        subcommands = {
                GetDpCommand.class,
                GetDpvCommand.class
        }
)
public class RegistryGetCommand implements Runnable {

    @ParentCommand
    protected RegistryCommands registryCommands;

    @Override
    public void run() { }

}
