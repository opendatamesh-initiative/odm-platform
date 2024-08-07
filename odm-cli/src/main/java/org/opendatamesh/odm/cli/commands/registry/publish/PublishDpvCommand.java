package org.opendatamesh.odm.cli.commands.registry.publish;

import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(
        name = "dpv",
        description = "Publish a Data Product Version",
        version = "odm-cli registry publish dpv 1.0.0",
        mixinStandardHelpOptions = true
)
public class PublishDpvCommand implements Runnable {

    @ParentCommand
    private RegistryPublishCommand registryPublishCommand;

    @Option(
            names = "--dpv-file",
            description = "Path of the JSON descriptor of the Data Product Version object",
            required = true
    )
    String dataProductVersionDescriptorPath;

    @Option(
            names = "--id",
            description = "ID of the Data Product",
            required = true
    )
    String dataProductId;

    @Override
    public void run() {
        String dpv;
        try {
            dpv = FileReaderUtils.readFileFromPath(dataProductVersionDescriptorPath);
        } catch (IOException e) {
            System.out.println(
                    "Impossible to read file [" + dataProductVersionDescriptorPath +
                            "]. Check if the file exists and retry"
            );
            return;
        }
        try {
            ResponseEntity<DataProductVersionDPDS> dataProductResponseEntity =
                    registryPublishCommand.registryCommands.getRegistryClient().postDataProductVersion(dataProductId, dpv);
            if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                DataProductVersionDPDS dataProductVersion = dataProductResponseEntity.getBody();
                System.out.println("Data Product Version CREATED:\n" + ObjectMapperUtils.formatAsString(dataProductVersion));
            }
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + dataProductResponseEntity.getStatusCode()
                );
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Registry server. Verify the URL and retry");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
