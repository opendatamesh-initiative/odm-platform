package org.opendatamesh.platform.pp.registry.core;

import java.io.File;
import java.net.URI;

import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;

import com.fasterxml.jackson.databind.JsonNode;

public class CoreApp {

    public static void main(String[] args) throws Exception {
        URI ROOT_DOC_LOACAL_URI = new File("src/test/resources/demo/tripexecution/data-product-descriptor.json")
                .toURI();
        URI ROOT_DOC_REMOTE_URI = new URI(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#zozzo?pippo=/xxx");

        DataProductVersionSource descriptorSource = new DataProductVersionSource(ROOT_DOC_REMOTE_URI);
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
