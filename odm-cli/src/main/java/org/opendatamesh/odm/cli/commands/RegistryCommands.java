package org.opendatamesh.odm.cli.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.conn.HttpHostConnectException;
import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.odm.cli.utils.PropertiesManager;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
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
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = PropertiesManager.getPropertyValue(properties, "registry-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The registry server URL wasn't specified. Use the -s option or create a file with the \"registry-server\" property");
            return;
        }

        try {
            registryClient = new RegistryClient(serverUrl);

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
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = PropertiesManager.getPropertyValue(properties, "registry-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The registry server URL wasn't specified. Use the -s option or create a file with the \"registry-server\" property");
            return;
        }

        try {
            registryClient = new RegistryClient(serverUrl);

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
    void getDpv(@Option(names = "--id", required = true) String id, @Option(names = "--version", required = true) String version) {
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = PropertiesManager.getPropertyValue(properties, "registry-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The registry server URL wasn't specified. Use the -s option or create a file with the \"registry-server\" property");
            return;
        }

        try {
            registryClient = new RegistryClient(serverUrl);

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


    @Override
    public void run() {    }

}

