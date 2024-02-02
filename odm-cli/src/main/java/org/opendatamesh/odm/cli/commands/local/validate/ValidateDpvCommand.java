package org.opendatamesh.odm.cli.commands.local.validate;

import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

@Command(
        name = "dpv",
        description = "Validate the syntax of a Data Product Version JSON descriptor",
        version = "odm-cli local validate dpv 1.0.0",
        mixinStandardHelpOptions = true
)
public class ValidateDpvCommand implements Runnable {

    @Option(
            names = {"-f", "--file"},
            description = "Path of the JSON descriptor of the Data Product Version object",
            required = true
    )
    String dataProductVersionDescriptorPath;

    @Override
    public void run() {
        try {
            String descriptorContent = FileReaders.readFileFromPath(dataProductVersionDescriptorPath);
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
        DPDSParser descriptorParser = new DPDSParser(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/schemas/",
                "1.0.0",
                null
        );
        ParseOptions options = new ParseOptions();

        descriptorParser.parse(descriptorLocation, options);

    }

}
