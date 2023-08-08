package org.opendatamesh.platform.core.dpds;

import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.GitLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.core.dpds.serde.DataProductVersionSerializer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

        //String ROOT_DOC_LOACAL_FILEPATH = "/home/andrea.gioia/Sviluppi/quantyca/open-data-mesh/github/odm-platform-pp-services/product-plane-services/registry-server/src/test/resources/test/dataproduct-descriptor/dp1-v1.json";
        //DescriptorLocation location = new UriLocation(Files.readString(Paths.get(ROOT_DOC_LOACAL_FILEPATH)));

        URI ROOT_DOC_REMOTE_URI = new URI(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#pippo?pippo=/xxx");
        DescriptorLocation location = new UriLocation(ROOT_DOC_REMOTE_URI);


        String repoUri = "git@ssh.dev.azure.com:v3/andreagioia/opendatamesh/odm-dpds-examples";
        URI descriptorUri = new URI("data-product-descriptor.json");
        //String repoUri = "git@github.com:opendatamesh-initiative/odm-specification-dpdescriptor.git";
        //URI descriptorUri = new URI("examples/tripexecution/data-product-descriptor.json");
        location = new GitLocation(repoUri, descriptorUri);
       
        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl( "http://localhost:80/");

        ParseResult result = parser.parse(location, options);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
        System.out.println(descriptor.getInfo().getVersionNumber());

        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String rawContent = serializer.serialize(descriptor, "canonical", "yaml", true);
        System.out.println(rawContent);

        List<PortDPDS> ports = new ArrayList<PortDPDS>();
        ports.addAll(descriptor.getInterfaceComponents().getOutputPorts());
        ports.addAll(descriptor.getInterfaceComponents().getObservabilityPorts());

        for (PortDPDS port : ports) {
            ApiDefinitionReferenceDPDS api = (ApiDefinitionReferenceDPDS) port.getPromises().getApi().getDefinition();
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
