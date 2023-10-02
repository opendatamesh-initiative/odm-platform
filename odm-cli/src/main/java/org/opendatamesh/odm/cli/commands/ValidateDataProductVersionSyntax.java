package org.opendatamesh.odm.cli.commands;

import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

@Command(
        name = "validate-dpv",
        description = "Validate the syntax of a Data Product Version",
        version = "odm-cli validate-dpv 1.0.0",
        mixinStandardHelpOptions = true
)
public class ValidateDataProductVersionSyntax implements Runnable {

    @Option(
            names = {"-f", "--file", "--path"},
            description = "Path of a Data Product Version file",
            required = true
    )
    private String filePath;

    @Override
    public void run() {
        try {
            String descriptorContet = readFile();
            validateDPV(descriptorContet);
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

    private String readFile() throws IOException {
        return FileReaders.readFileFromPath(filePath);
    }

    private void validateDPV(String descriptorContent) throws ParseException {

        System.out.println("Validating file ...");

        UriLocation descriptorLocation = new UriLocation(descriptorContent);
        DPDSParser descriptorParser = new DPDSParser();
        ParseOptions options = new ParseOptions();

        descriptorParser.parse(descriptorLocation, options);

    }


}
