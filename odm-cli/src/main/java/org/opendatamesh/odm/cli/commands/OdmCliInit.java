package org.opendatamesh.odm.cli.commands;

import org.opendatamesh.odm.cli.commands.blueprint.BlueprintCommands;
import org.opendatamesh.odm.cli.commands.devops.DevOpsCommands;
import org.opendatamesh.odm.cli.commands.local.LocalCommands;
import org.opendatamesh.odm.cli.commands.policy.PolicyCommands;
import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "odm-cli",
        description = "ODM CLI init method",
        version = "odm-cli 1.0.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                LocalCommands.class,
                RegistryCommands.class,
                BlueprintCommands.class,
                PolicyCommands.class,
                DevOpsCommands.class
        }
)
public class OdmCliInit implements Runnable {

    public static void main(String[] args) {
        CommandLine.run(new OdmCliInit(), args);
    }

    @Override
    public void run() { }

}
