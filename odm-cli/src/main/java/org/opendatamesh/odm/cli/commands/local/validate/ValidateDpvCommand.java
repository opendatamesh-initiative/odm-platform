package org.opendatamesh.odm.cli.commands.local.validate;

import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.odm.cli.utils.PrintUtils;
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
            String descriptorContent = FileReaderUtils.readFileFromPath(dataProductVersionDescriptorPath);
            validateDPV(descriptorContent);
            System.out.println("\nValid Data Product Version");
        } catch (IOException e) {
            System.out.println("\nInvalid Data Product Version");
            RuntimeException exception = new RuntimeException(
                    "Error parsing Data Product Version file: " + e.getMessage()
            );
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        } catch (ParseException e) {
            System.out.println("\nInvalid Data Product Version");
            RuntimeException exception = new RuntimeException("Data Product Version not valid: " + e.getMessage());
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        } catch (Exception e) {
            System.out.println("\nInvalid Data Product Version");
            RuntimeException exception = new RuntimeException(
                    "Generic error validationg Data Product Version: " + e.getMessage()
                            + ". Check for missing parts of the descriptor."
            );
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        }
    }

    private void validateDPV(String descriptorContent) throws Exception {

        System.out.println("Validating file ...");

        UriLocation descriptorLocation = new UriLocation(descriptorContent);
        DPDSParser descriptorParser = new DPDSParser(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/schemas/",
                "1.0.0",
                null
        );
        ParseOptions options = new ParseOptions();

        PrintUtils.silentExecution(
                () -> {
                    descriptorParser.parse(descriptorLocation, options);
                }
        );

    }

}
