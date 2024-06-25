package org.opendatamesh.odm.cli.commands.registry.get;

import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "dpv",
        description = "Get a specific Data Product Version",
        version = "odm-cli registry get dpv 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetDpvCommand implements Runnable {

    @ParentCommand
    private RegistryGetCommand registryGetCommand;

    @Option(
            names = "--id",
            description = "ID of the Data Product",
            required = true
    )
    String dataProductId;

    @Option(
            names = "--dpv",
            description = "Version of the Data Product",
            required = true
    )
    String dataProductVersion;

    @Override
    public void run() {
        try {
            ResponseEntity<DataProductVersionDPDS> dataProductVersionsResponseEntity =
                    registryGetCommand.registryCommands.getRegistryClient().getDataProductVersion(
                            dataProductId, dataProductVersion
                    );
            if(dataProductVersionsResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                DataProductVersionDPDS dataProductVersion = dataProductVersionsResponseEntity.getBody();
                System.out.println(ObjectMapperUtils.formatAsString(dataProductVersion));
            }
            else if(dataProductVersionsResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println(
                        "Version [" + dataProductVersion + "] of Data product with ID [" + dataProductId +"] not found"
                );
            else
                System.out.println(
                        "Got an unexpected response. Error code: " + dataProductVersionsResponseEntity.getStatusCode()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
