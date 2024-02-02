package org.opendatamesh.odm.cli.commands.registry.publish;

import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
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
    private PublishCommand publishCommand;

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

        String dpv = null;

        try {
            dpv = FileReaders.readFileFromPath(dataProductVersionDescriptorPath);
        } catch (IOException e) {
            System.out.println(
                    "Impossible to read file [" + dataProductVersionDescriptorPath +
                            "]. Check if the file exists and retry"
            );
            return;
        }

        try {

            ResponseEntity<DataProductVersionDPDS> dataProductResponseEntity =
                    publishCommand.registryCommands.getRegistryClient().postDataProductVersion(dataProductId, dpv);

            if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                DataProductVersionDPDS dataProductVersion = dataProductResponseEntity.getBody();
                System.out.println("Data Product Version CREATED:\n" + dataProductVersion.toEventString());
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
