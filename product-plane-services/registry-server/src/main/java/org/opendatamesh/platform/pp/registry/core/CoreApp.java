package org.opendatamesh.platform.pp.registry.core;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.opendatamesh.platform.pp.registry.core.dsapi.DataStoreApiParser;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.PortResource;
import org.opendatamesh.platform.pp.registry.resources.v1.shared.DataServiceApiResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Service;

@Service
public class CoreApp implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CoreApp.class)
        .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
        .run(args);
     }

    @Override
    public void run(String... arg0) throws Exception {
        
        String ROOT_DOC_LOACAL_FILEPATH = "/home/andrea.gioia/Sviluppi/quantyca/open-data-mesh/github/odm-platform-pp-services/product-plane-services/registry-server/src/test/resources/test/dataproduct-descriptor/dp1-v1.json";
        //DataProductVersionSource descriptorSource = new DataProductVersionSource(Files.readString(Paths.get(ROOT_DOC_LOACAL_FILEPATH)));

        URI ROOT_DOC_REMOTE_URI = new URI("https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#zozzo?pippo=/xxx");
        DataProductVersionSource dataProductVersionSource = new DataProductVersionSource(ROOT_DOC_REMOTE_URI);
       
        DataProductVersionBuilder builder = new DataProductVersionBuilder(dataProductVersionSource,
                "http://localhost:80/");

        DataProductVersionResource dataProductVerionRes = null;
        dataProductVerionRes = builder.build(true);

        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String rawContent = serializer.serialize(dataProductVerionRes, "canonical", "yaml", true);
        System.out.println(rawContent);

        List<PortResource> outputPorts = dataProductVerionRes.getInterfaceComponents().getOutputPorts();
        for(PortResource outputPort : outputPorts) {
            if(outputPort.getPromises().getApi().getSpecification().equalsIgnoreCase("datastoreApi")) {
                String apiRawContent = outputPort.getPromises().getApi().getDefinition().getRawContent();
                DataStoreApiParser dataStoreApiParser = new DataStoreApiParser(dataProductVersionSource.getRootDocBaseURI());
                DataServiceApiResource api = dataStoreApiParser.parse(apiRawContent);
                System.out.println("\n\n====\n" + outputPort.getFullyQualifiedName() + "\n====\n\n" +  api.getEndpoints());
            } else {
                System.out.println("\n\n====\n" + outputPort.getFullyQualifiedName() + "\n====\n\n" + outputPort.getPromises().getApi().getSpecification() + " not supported");
            }
            
        }
    }
}
