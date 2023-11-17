package org.opendatamesh.odm.cli.commands;

import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.pp.blueprint.api.clients.BlueprintClient;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;


@Command(
        name = "local",
        description = "use the cli for local functionalities",
        version = "odm-cli local 1.0.0",
        mixinStandardHelpOptions = true
)
public class LocalCommands implements Runnable {

    BlueprintClient blueprintClient;

    public static void main(String[] args) {
        CommandLine.run(new LocalCommands(), args);
    }

    @Command(
            name = "validate-dpv",
            description = "Validate the syntax of a Data Product Version given the path of a Data Product Version file",
            version = "odm-cli local validate-dpv 1.0.0",
            mixinStandardHelpOptions = true
    )
    void validateDpv(@CommandLine.Option(
            names = {"-f", "--file"},
            description = "Path of a Data Product Version file",
            required = true
    )String filePath) {
        try {
            String descriptorContent = FileReaders.readFileFromPath(filePath);
            validateDPV(descriptorContent);
            System.out.println("\nValid Data Product Version");
        } catch (IOException e) {
            RuntimeException exception = new RuntimeException(e.getMessage());
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        } catch (ParseException e) {
            RuntimeException exception = new RuntimeException("Data Product Version not valid: " + e.getMessage());
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        }
    }

    private void validateDPV(String descriptorContent) throws ParseException {

        System.out.println("Validating file ...");

        UriLocation descriptorLocation = new UriLocation(descriptorContent);
        DPDSParser descriptorParser = new DPDSParser();
        ParseOptions options = new ParseOptions();

        descriptorParser.parse(descriptorLocation, options);

    }

    @Override
    public void run() {
        System.out.println("Allows to communicate with blueprint module");
    }

}

