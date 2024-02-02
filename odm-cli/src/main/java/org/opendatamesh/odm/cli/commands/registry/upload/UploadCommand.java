package org.opendatamesh.odm.cli.commands.registry.upload;

import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "upload",
        description = "Commands to upload objects",
        mixinStandardHelpOptions = true,
        subcommands = {
                UploadDpvCommand.class
        }
)
public class UploadCommand implements Runnable {

    @ParentCommand
    protected RegistryCommands registryCommands;

    @Override
    public void run() { }

}
