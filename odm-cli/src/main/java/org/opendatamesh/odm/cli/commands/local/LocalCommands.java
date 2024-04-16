package org.opendatamesh.odm.cli.commands.local;

import org.opendatamesh.odm.cli.commands.local.validate.LocalValidateCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "local",
        description = "use the cli for local functionalities",
        version = "odm-cli local 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                LocalValidateCommand.class
        }
)
public class LocalCommands implements Runnable {

    public static void main(String[] args) {
        CommandLine.run(new LocalCommands(), args);
    }

    @Override
    public void run() { }

}

