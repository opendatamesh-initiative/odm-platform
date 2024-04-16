package org.opendatamesh.odm.cli.commands.registry.upload;

import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "upload",
        description = "Commands to upload objects",
        mixinStandardHelpOptions = true,
        version = "odm-cli registry upload 1.0.0",
        subcommands = {
                UploadDpvCommand.class
        }
)
public class RegistryUploadCommand implements Runnable {

    @ParentCommand
    protected RegistryCommands registryCommands;

    @Override
    public void run() { }

}
