package org.opendatamesh.odm.cli;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class OdmCliApplication {
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new HelloWorld()); // Default command
        cmd.addSubcommand("hello", new HelloWorld());
        cmd.addSubcommand("hello-two", new HelloWorldTwo());

        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

}
