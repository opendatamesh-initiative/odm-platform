package org.opendatamesh.platform.core.dpds;

import java.net.URI;
import java.util.List;

import org.opendatamesh.platform.core.dpds.asyncapi.AsyncApiParser;
import org.opendatamesh.platform.core.dpds.dsapi.DataStoreApiParser;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.serde.DataProductVersionSerializer;

/* 
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Service;
*/

public class CoreApp /*implements CommandLineRunner*/ {

    public static void main(String[] args) throws Exception {
        /* 
        new SpringApplicationBuilder(CoreApp.class)
        .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
        .run(args);
        */
        run(args);
     }

    //@Override
    public static void run(String... arg0) throws Exception {
        
        String ROOT_DOC_LOACAL_FILEPATH = "/home/andrea.gioia/Sviluppi/quantyca/open-data-mesh/github/odm-platform-pp-services/product-plane-services/registry-server/src/test/resources/test/dataproduct-descriptor/dp1-v1.json";
        //DataProductVersionSource descriptorSource = new DataProductVersionSource(Files.readString(Paths.get(ROOT_DOC_LOACAL_FILEPATH)));

        URI ROOT_DOC_REMOTE_URI = new URI("https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#zozzo?pippo=/xxx");
        DataProductVersionSource dataProductVersionSource = new DataProductVersionSource(ROOT_DOC_REMOTE_URI);
       
        DataProductVersionBuilder builder = new DataProductVersionBuilder(dataProductVersionSource,
                "http://localhost:80/");

        DataProductVersionDPDS dataProductVerionRes = null;
        dataProductVerionRes = builder.build(true);

        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String rawContent = serializer.serialize(dataProductVerionRes, "canonical", "yaml", true);
        System.out.println(rawContent);

        List<PortDPDS> outputPorts = dataProductVerionRes.getInterfaceComponents().getOutputPorts();
        for(PortDPDS outputPort : outputPorts) {
            if(outputPort.getPromises().getApi().getSpecification().equalsIgnoreCase("datastoreApi")) {
                String apiRawContent = outputPort.getPromises().getApi().getDefinition().getRawContent();
                DataStoreApiParser dataStoreApiParser = new DataStoreApiParser(dataProductVersionSource.getRootDocBaseURI());
                ApiDefinitionDPDS api = dataStoreApiParser.parse(apiRawContent);
                System.out.println("\n\n====\n" + outputPort.getFullyQualifiedName() + "\n====\n\n" +  api.getEndpoints());
            } else if(outputPort.getPromises().getApi().getSpecification().equalsIgnoreCase("asyncApi")){
                String apiRawContent = outputPort.getPromises().getApi().getDefinition().getRawContent();
                AsyncApiParser asyncApiParser = new AsyncApiParser(dataProductVersionSource.getRootDocBaseURI());
                ApiDefinitionDPDS api = asyncApiParser.parse(apiRawContent);
                System.out.println("\n\n====\n" + outputPort.getFullyQualifiedName() + "\n====\n\n" +  api.getEndpoints());
            }else {
                System.out.println("\n\n====\n" + outputPort.getFullyQualifiedName() + "\n====\n\n" + outputPort.getPromises().getApi().getSpecification() + " not supported");
            }
            
        }
    }
}
