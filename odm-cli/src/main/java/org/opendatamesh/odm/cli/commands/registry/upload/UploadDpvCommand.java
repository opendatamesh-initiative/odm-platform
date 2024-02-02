package org.opendatamesh.odm.cli.commands.registry.upload;

import org.opendatamesh.odm.cli.commands.registry.RegistryCommands;
import org.opendatamesh.odm.cli.utils.InputManager;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductDescriptorLocationResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "dpv",
        description = "Upload a Data Product Version from a git repository",
        version = "odm-cli registry upload dpv 1.0.0",
        mixinStandardHelpOptions = true
)
public class UploadDpvCommand implements Runnable {

    @ParentCommand
    private UploadCommand uploadCommand;

    @Override
    public void run() {

        DataProductDescriptorLocationResource dpLocation = new DataProductDescriptorLocationResource();
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();

        //Request input from user
        String repositorySshUri = InputManager.getValueFromUser("Insert repository ssh URI: ");
        git.setRepositorySshUri(repositorySshUri);

        String branch = InputManager.getValueFromUser(
                "Insert branch (blank for \"main\"): ", "main"
        );
        git.setBranch(branch);
        dpLocation.setGit(git);

        String rootDocumentUri = InputManager.getValueFromUser(
                "Insert the root document URI (inside the repo you previously specified): "
        );
        dpLocation.setRootDocumentUri(rootDocumentUri);


        try {

            ResponseEntity<String> dataProductResponseEntity =
                    uploadCommand.registryCommands.getRegistryClient().uploadDataProductVersion(
                            dpLocation, String.class
                    );

            if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                String dataProductVersion = dataProductResponseEntity.getBody();
                System.out.println("Data product version CREATED:\n" + dataProductVersion);
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
