package org.opendatamesh.odm.cli.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.odm.cli.utils.FileReaders;
import org.opendatamesh.odm.cli.utils.PropertiesManager;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.pp.blueprint.api.clients.BlueprintClient;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.Properties;


@Command(
        name = "registry",
        description = "allows to communicate with blueprint module",
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
            name = "listDPV",
            description = "Lists all the available Data Products",
            version = "odm-cli registry listDPV 1.0.0",
            mixinStandardHelpOptions = true
    )
    void listDpv() {
        Properties properties = null;
        try {
            properties = FileReaders.getPropertiesFromFilePath(propertiesFileOption);
        } catch (IOException e) {
            System.out.println("No properties file has been found");
        }

        String serverUrl = PropertiesManager.getPropertyValue(properties, "registry-server", serverUrlOption);
        if (serverUrl == null) {
            System.out.println("The registry server URL wasn't specified. Use the -s option or create a file with the \"blueprint-server\" property");
            return;
        }

        try {
            registryClient = new RegistryClient(serverUrl);

            ResponseEntity<DataProductResource[]> dataProductResourceResponseEntity= registryClient.getDataProducts();
            DataProductResource[] dataProducts = dataProductResourceResponseEntity.getBody();
            for(DataProductResource dataProduct : dataProducts)
                System.out.println(dataProduct.toEventString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        System.out.println("Allows to communicate with blueprint module");
    }

}

