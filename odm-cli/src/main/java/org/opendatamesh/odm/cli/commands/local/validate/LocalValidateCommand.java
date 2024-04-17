package org.opendatamesh.odm.cli.commands.local.validate;

import picocli.CommandLine.Command;

@Command(
        name = "validate",
        description = "Validate descriptors",
        version = "odm-cli local validate 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                ValidateDpvCommand.class
        }
)
public class LocalValidateCommand implements Runnable {

    @Override
    public void run() { }

}
