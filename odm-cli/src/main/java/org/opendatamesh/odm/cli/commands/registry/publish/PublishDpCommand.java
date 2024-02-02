package org.opendatamesh.odm.cli.commands.registry.publish;

import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(
        name = "dp",
        description = "Publish a Data Product",
        version = "odm-cli registry publish dp 1.0.0",
        mixinStandardHelpOptions = true
)
public class PublishDpCommand implements Runnable {

    @ParentCommand
    private PublishCommand publishCommand;

    @Option(
            names = "--dpv-file",
            description = "Path of the JSON descriptor of the Data Product Version object",
            required = true
    )
    String dataProductDescriptorPath;

    @Override
    public void run() {

        String dp = null;

        try {
            dp = FileReaderUtils.readFileFromPath(dataProductDescriptorPath);
        } catch (IOException e) {
            System.out.println(
                    "Impossible to read file [" + dataProductDescriptorPath + "]. Check if the file exists and retry"
            );
            return;
        }

        try {

            ResponseEntity<DataProductResource> dataProductResponseEntity =
                    publishCommand.registryCommands.getRegistryClient().postDataProduct(dp);

            if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                DataProductResource dataProduct = dataProductResponseEntity.getBody();
                System.out.println("Data product CREATED:\n" + dataProduct.toEventString());
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
