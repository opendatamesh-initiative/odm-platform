package org.opendatamesh.odm.cli.commands.registry.publish;

import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "publish",
        description = "Commands to publish data products and versions",
        version = "odm-cli registry publish 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                PublishDpCommand.class,
                PublishDpvCommand.class
        }
)
public class RegistryPublishCommand implements Runnable {

    @ParentCommand
    protected RegistryCommands registryCommands;

    @Override
    public void run() { }

}
