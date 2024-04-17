package org.opendatamesh.odm.cli.commands.registry.list;

import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "dpv",
        description = "Lists all the available Data Product Versions given a Data Product ID",
        version = "odm-cli registry list dp 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListDpvCommand implements Runnable {

    @ParentCommand
    private RegistryListCommand registryListCommand;

    @Option(
            names = "--id",
            description = "ID of the Data Product",
            required = true
    )
    String dataProductId;

    @Override
    public void run() {
        try {
            ResponseEntity<DataProductResource> dataProductResourceResponseEntity =
                    registryListCommand.registryCommands.getRegistryClient().getDataProduct(dataProductId);
            if(!dataProductResourceResponseEntity.getStatusCode().is2xxSuccessful()) {
                System.out.println("Data Product [" + dataProductId +"] not found.");
                return;
            }
            ResponseEntity<String[]> dataProductVersionsResponseEntity =
                    registryListCommand.registryCommands.getRegistryClient().getDataProductVersions(dataProductId);
            if(dataProductVersionsResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                String[] dataProductVersions = dataProductVersionsResponseEntity.getBody();
                if (dataProductVersions.length == 0)
                    System.out.println("[]");
                for (String dataProductVersion : dataProductVersions)
                    System.out.println(dataProductVersion);
            }
            else
                System.out.println("Error in response from Registry Server");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
