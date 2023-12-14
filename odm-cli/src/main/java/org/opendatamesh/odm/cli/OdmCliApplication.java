package org.opendatamesh.odm.cli;

import ch.qos.logback.classic.LoggerContext;
import org.opendatamesh.odm.cli.commands.*;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class OdmCliApplication {

    public static void main(String[] args) {
        //Disabling logging
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.getLogger("org").setLevel(ch.qos.logback.classic.Level.OFF);

        // Init
        CommandLine odmCliCommand = new CommandLine(new OdmCliInit());

        // Local commands
        odmCliCommand.addSubcommand("local", new LocalCommands());

        // Registry commands
        odmCliCommand.addSubcommand("registry", new RegistryCommands());

        // Blueprint commands
        odmCliCommand.addSubcommand("blueprint", new BlueprintCommands());

        odmCliCommand.setExecutionStrategy(new CommandLine.RunAll());
        int exitCode = odmCliCommand.execute(args);
        System.exit(exitCode);
    }

}
