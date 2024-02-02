package org.opendatamesh.odm.cli.commands.registry.list;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "dp",
        description = "Lists all available Data Products",
        version = "odm-cli registry list dp 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListDpCommand implements Runnable {

    @ParentCommand
    private ListCommand listCommand;

    @Override
    public void run() {
        try {

            ResponseEntity<DataProductResource[]> dataProductResourceResponseEntity =
                    listCommand.registryCommands.getRegistryClient().getDataProducts();

            if(dataProductResourceResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                DataProductResource[] dataProducts = dataProductResourceResponseEntity.getBody();
                if (dataProducts.length == 0)
                    System.out.println("[]");
                for (DataProductResource dataProduct : dataProducts)
                    System.out.println(dataProduct.toEventString());
            }
            else
                System.out.println("Error in response from Registry Server");

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Registry server. Verify the URL and retry");
        }

    }

}
