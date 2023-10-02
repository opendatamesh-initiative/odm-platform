package org.opendatamesh.odm.cli;

import org.opendatamesh.odm.cli.commands.HelloWorld;
import org.opendatamesh.odm.cli.commands.OdmCliInit;
import org.opendatamesh.odm.cli.commands.ValidateDataProductVersionSyntax;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class OdmCliApplication {
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new OdmCliInit()); // Default command
        cmd.addSubcommand("hello", new HelloWorld());
        cmd.addSubcommand("validate-dpv", new ValidateDataProductVersionSyntax());

        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

}
