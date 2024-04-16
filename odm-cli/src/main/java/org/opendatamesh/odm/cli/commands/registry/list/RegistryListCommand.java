package org.opendatamesh.odm.cli.commands.registry.list;

import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "list",
        description = "Commands to list data products and versions",
        mixinStandardHelpOptions = true,
        version = "odm-cli registry list 1.0.0",
        subcommands = {
                ListDpCommand.class,
                ListDpvCommand.class
        }
)
public class RegistryListCommand implements Runnable {

    @ParentCommand
    protected RegistryCommands registryCommands;

    @Override
    public void run() {}

}
