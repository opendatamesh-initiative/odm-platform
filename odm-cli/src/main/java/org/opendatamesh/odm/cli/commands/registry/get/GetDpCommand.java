package org.opendatamesh.odm.cli.commands.registry.get;

import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "dp",
        description = "Get a specific Data Product",
        version = "odm-cli registry get dp 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetDpCommand implements Runnable {

    @ParentCommand
    private RegistryGetCommand registryGetCommand;

    @Option(
            names = "--id",
            description = "ID of the Data Product",
            required = true
    )
    String dataProductId;

    @Override
    public void run() {
        try {
            ResponseEntity<DataProductResource> dataProductResponseEntity =
                    registryGetCommand.registryCommands.getRegistryClient().getDataProduct(dataProductId);
            if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                DataProductResource dataProduct = dataProductResponseEntity.getBody();
                System.out.println(ObjectMapperUtils.formatAsString(dataProduct));
            }
            else if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Data product with ID [" + dataProductId + "] not found");
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + dataProductResponseEntity.getStatusCode()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
