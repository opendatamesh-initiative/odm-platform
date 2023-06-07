package org.opendatamesh.platform.pp.registry.core;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;

import com.fasterxml.jackson.databind.JsonNode;

public class CoreApp {


    public static void main(String[] args) throws Exception {
        String ROOT_DOC_LOACAL_FILEPATH = "/home/andrea.gioia/Sviluppi/quantyca/open-data-mesh/github/odm-platform-pp-services/product-plane-services/registry-server/src/test/resources/test/dataproduct-descriptor/dp1-v1.json";
        URI ROOT_DOC_REMOTE_URI = new URI(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#zozzo?pippo=/xxx");

        //DataProductVersionSource descriptorSource = new DataProductVersionSource(ROOT_DOC_REMOTE_URI);
        DataProductVersionSource descriptorSource = new DataProductVersionSource(Files.readString(Paths.get(ROOT_DOC_LOACAL_FILEPATH)));
       
        DataProductVersionBuilder descriptorBuilder = 
                new DataProductVersionBuilder(descriptorSource , "http://localhost:80/");
           
        DataProductVersionResource descriptor = null;
        descriptor = descriptorBuilder.build(true);
        
        //String rawContent = descriptor.getParsedContent().getComponentRawContentOrdered(false);
        DataProductVersionMapper mapper = DataProductVersionMapper.getMapper();
        String rawContent = mapper.getRawContent(descriptor, false);
        JsonNode jsonNode = mapper.readTree(rawContent);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        System.out.println(json);
    }
}
