package org.opendatamesh.odm.cli;

import org.opendatamesh.odm.cli.commands.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class OdmCliApplication {

    public static void main(String[] args) {

        CommandLine odmCliCommand = new CommandLine(new OdmCliInit());
        odmCliCommand.addSubcommand("hello", new HelloWorld());

        // blueprint
        CommandLine blueprintCommand = odmCliCommand.addSubcommand("blueprint", new BlueprintCommands());


        //registry
        odmCliCommand.addSubcommand("registry", new RegistryCommands());

        //local
        odmCliCommand.addSubcommand("local", new LocalCommands());

        odmCliCommand.setExecutionStrategy(new CommandLine.RunAll());
        int exitCode = odmCliCommand.execute(args);
        System.exit(exitCode);
    }

}
