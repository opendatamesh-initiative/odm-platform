package org.opendatamesh.odm.cli.commands.registry.list;

import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "list",
        description = "Commands to list data products and versions",
        mixinStandardHelpOptions = true,
        subcommands = {
                ListDpCommand.class,
                ListDpvCommand.class
        }
)
public class ListCommand implements Runnable {

    @ParentCommand
    protected RegistryCommands registryCommands;

    @Override
    public void run() {}

}
