package org.opendatamesh.odm.cli;

import ch.qos.logback.classic.LoggerContext;
import org.opendatamesh.odm.cli.commands.OdmCliInit;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class OdmCliApplication {

    public static void main(String[] args) {

        // Disable default logs
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.getLogger("org").setLevel(ch.qos.logback.classic.Level.OFF);

        // Init
        CommandLine odmCliCommand = new CommandLine(new OdmCliInit());

        // Set execution strategy
        odmCliCommand.setExecutionStrategy(new CommandLine.RunAll());

        // Execute required command
        int exitCode = odmCliCommand.execute(args);
        System.exit(exitCode);

    }

}
