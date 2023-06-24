package org.opendatamesh.platform.core.dpds;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.opendatamesh.platform.core.dpds.api.asyncapi.AsyncApiParser;
import org.opendatamesh.platform.core.dpds.api.dsapi.DataStoreApiParser;
import org.opendatamesh.platform.core.dpds.api.openapi.OpenApiParser;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionDPDS;
import org.opendatamesh.platform.core.dpds.serde.DataProductVersionSerializer;

/* 
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Service;
*/

public class CoreApp /* implements CommandLineRunner */ {

    public static void main(String[] args) throws Exception {
        /*
         * new SpringApplicationBuilder(CoreApp.class)
         * .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
         * .run(args);
         */
        run(args);
    }

    // @Override
    public static void run(String... arg0) throws Exception {

        String ROOT_DOC_LOACAL_FILEPATH = "/home/andrea.gioia/Sviluppi/quantyca/open-data-mesh/github/odm-platform-pp-services/product-plane-services/registry-server/src/test/resources/test/dataproduct-descriptor/dp1-v1.json";
        // DataProductVersionSource descriptorSource = new
        // DataProductVersionSource(Files.readString(Paths.get(ROOT_DOC_LOACAL_FILEPATH)));

        URI ROOT_DOC_REMOTE_URI = new URI(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#pippo?pippo=/xxx");
        DataProductVersionSource dataProductVersionSource = new DataProductVersionSource(ROOT_DOC_REMOTE_URI);

        DPDSParser parser = new DPDSParser(dataProductVersionSource,
                "http://localhost:80/");

        DataProductVersionDPDS dataProductVerion = null;
        dataProductVerion = parser.parse(true);

        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String rawContent = serializer.serialize(dataProductVerion, "canonical", "yaml", true);
        System.out.println(rawContent);

        List<PortDPDS> ports = new ArrayList<PortDPDS>();
        ports.addAll(dataProductVerion.getInterfaceComponents().getOutputPorts());
        ports.addAll(dataProductVerion.getInterfaceComponents().getObservabilityPorts());

        for (PortDPDS port : ports) {
            ApiDefinitionDPDS api = (ApiDefinitionDPDS) port.getPromises().getApi().getDefinition();
            System.out.println("\n\n====\n" + port.getFullyQualifiedName() + "\n====\n\n" + api.getEndpoints());
        }

        /* 
        for(PortDPDS port : ports) {
            String apiRawContent = port.getPromises().getApi().getDefinition().getRawContent();
            String mediaType = port.getPromises().getApi().getDefinition().getMediaType();
            String specification =  port.getPromises().getApi().getSpecification();
            if("datastoreApi".equalsIgnoreCase(specification)) {
                DataStoreApiParser dataStoreApiParser = new DataStoreApiParser(dataProductVersionSource.getRootDocBaseURI());
                ApiDefinitionDPDS api = dataStoreApiParser.parse(apiRawContent, mediaType);
                System.out.println("\n\n====\n" + port.getFullyQualifiedName() + "\n====\n\n" +  api.getEndpoints());
            } else if("asyncApi".equalsIgnoreCase(specification)){
                AsyncApiParser asyncApiParser = new AsyncApiParser(dataProductVersionSource.getRootDocBaseURI());
                ApiDefinitionDPDS api = asyncApiParser.parse(apiRawContent, mediaType);
                System.out.println("\n\n====\n" + port.getFullyQualifiedName() + "\n====\n\n" +  api.getEndpoints());
            } else if("openApi".equalsIgnoreCase(specification)){
                OpenApiParser openApiParser = new OpenApiParser(dataProductVersionSource.getRootDocBaseURI());
                ApiDefinitionDPDS api = openApiParser.parse(apiRawContent, mediaType);
                System.out.println("\n\n====\n" + port.getFullyQualifiedName() + "\n====\n\n" +  api.getEndpoints());
            } else {
                System.out.println("\n\n====\n" + port.getFullyQualifiedName() + "\n====\n\n" + port.getPromises().getApi().getSpecification() + " not supported");
            }
            */

    }
}
