package org.opendatamesh.odm.cli.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.odm.cli.utils.InputManager;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductDescriptorLocationResource;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.Properties;


@Command(
        name = "registry",
        description = "allows to communicate with registry module",
        version = "odm-cli registry 1.0.0",
        mixinStandardHelpOptions = true
)
public class RegistryCommands implements Runnable {

     RegistryClient registryClient;

    @Option(names = {"-s", "--server"},
            description = "URL of the Registry server. It must include the port. It overrides the value inside the properties file, if it is present",
            required = false)
    String serverUrlOption;

    @Option(names = {"-f", "--properties-file"},
            description = "Path to the properties file",
            defaultValue = "./properties.yml",
            required = false)
    String propertiesFileOption;

    public static void main(String[] args) {
        CommandLine.run(new RegistryCommands(), args);
    }

    @Command(
            name = "listDP",
            description = "Lists all the available Data Products",
            version = "odm-cli registry listDP 1.0.0",
            mixinStandardHelpOptions = true
    )
    void listDp() {
        try {
            registryClient = setUpRegistryClient();

            ResponseEntity<DataProductResource[]> dataProductResourceResponseEntity= registryClient.getDataProducts();
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
            return;
        }
    }

    @Command(
            name = "listDPV",
            description = "Lists all the available Versions for one Data Product (Given its id)",
            version = "odm-cli registry listDPV 1.0.0",
            mixinStandardHelpOptions = true
    )
    void listDpv(@Option(names = "--id", required = true) String id) {
        try {
            registryClient = setUpRegistryClient();

            ResponseEntity<String[]> dataProductVersionsResponseEntity= registryClient.getDataProductVersions(id);
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

    @Command(
            name = "getDPV",
            description = "Get a specific Version of one Data Product (Given its id and version number)",
            version = "odm-cli registry getDPV 1.0.0",
            mixinStandardHelpOptions = true
    )
    void getDpv(@Option(names = "--id", required = true) String id, @Option(names = "-v", required = true) String version) {
        try {
            registryClient = setUpRegistryClient();

            ResponseEntity<DataProductVersionDPDS> dataProductVersionsResponseEntity= registryClient.getDataProductVersion(id, version);
            if(dataProductVersionsResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                DataProductVersionDPDS dataProductVersion = dataProductVersionsResponseEntity.getBody();

                System.out.println(dataProductVersion.toEventString());
            }
            else if(dataProductVersionsResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Data product with id " +id+ " and version " + version + " not found");
            else
                System.out.println("Got an unexpected response. Error code: " + dataProductVersionsResponseEntity.getStatusCode());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Command(
            name = "publishDP",
            description = "Publish a Data Product providing a file path",
            version = "odm-cli registry publishDP 1.0.0",
            mixinStandardHelpOptions = true
    )
    void publishDP(@Option(names = "--dp-file", required = true) String path) {
        String dp = null;
        try {
            dp = FileReaders.readFileFromPath(path);
        } catch (IOException e) {
            System.out.println("Impossible to read file \"" + path + "\". Check if the file exists and retry");
            return;
        }

        try {
            registryClient = setUpRegistryClient();

            ResponseEntity<DataProductResource> dataProductResponseEntity= registryClient.postDataProduct(dp);
            if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                DataProductResource dataProduct = dataProductResponseEntity.getBody();
                System.out.println("Data product CREATED:\n" + dataProduct.toEventString());
            }
            else
                System.out.println("Got an unexpected response. Error code: " + dataProductResponseEntity.getStatusCode());

        } catch (ResourceAccessException e) {
        System.out.println("Impossible to connect with Registry server. Verify the URL and retry");
        return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Command(
            name = "publishDPV",
            description = "Publish a Data Product Version providing a file path and the Data Product ID",
            version = "odm-cli registry publishDPV 1.0.0",
            mixinStandardHelpOptions = true
    )
    void publishDPV(@Option(names = "--dpv-file", required = true) String path, @Option(names = "--id", required = true) String id) {
        String dpv = null;
        try {
            dpv = FileReaders.readFileFromPath(path);
        } catch (IOException e) {
            System.out.println("Impossible to read file \"" + path + "\". Check if the file exists and retry");
            return;
        }

        try {
            registryClient = setUpRegistryClient();

            ResponseEntity<DataProductVersionDPDS> dataProductResponseEntity= registryClient.postDataProductVersion(id, dpv);
            if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                DataProductVersionDPDS dataProductVersion = dataProductResponseEntity.getBody();
                System.out.println("Data product version CREATED:\n" + dataProductVersion.toEventString());
            }
            else
                System.out.println("Got an unexpected response. Error code: " + dataProductResponseEntity.getStatusCode());

        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Registry server. Verify the URL and retry");
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Command(
            name = "uploadDPV",
            description = "Upload a Data Product Version from a git repository",
            version = "odm-cli registry uploadDPV 1.0.0",
            mixinStandardHelpOptions = true
    )
    void uploadDPV() {
        DataProductDescriptorLocationResource dpLocation = new DataProductDescriptorLocationResource();
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();

        //Request input from user
        String repositorySshUri = InputManager.getValueFromUser("Insert repository ssh URI: ");
        git.setRepositorySshUri(repositorySshUri);

        String branch = InputManager.getValueFromUser("Insert branch (blank for \"main\"): ", "main");
        git.setBranch(branch);
        dpLocation.setGit(git);

        String rootDocumentUri = InputManager.getValueFromUser("Insert the root document URI (inside the repo you previously specified): ");
        dpLocation.setRootDocumentUri(rootDocumentUri);


        try {
            registryClient = setUpRegistryClient();

            ResponseEntity<String> dataProductResponseEntity= registryClient.uploadDataProductVersion(dpLocation, String.class);
            if(dataProductResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                String dataProductVersion = dataProductResponseEntity.getBody();
                System.out.println("Data product version CREATED:\n" + dataProductVersion);
            }
            else
                System.out.println("Got an unexpected response. Error code: " + dataProductResponseEntity.getStatusCode());

        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Registry server. Verify the URL and retry");
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected RegistryClient setUpRegistryClient(){
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = InputManager.getPropertyValue(properties, "registry-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The registry server URL wasn't specified. Use the -s option or create a file with the \"registry-server\" property");
            throw new RuntimeException("The registry server URL wasn't specified");
        }

        return new RegistryClient(serverUrl);
    }



    @Override
    public void run() {    }

}

