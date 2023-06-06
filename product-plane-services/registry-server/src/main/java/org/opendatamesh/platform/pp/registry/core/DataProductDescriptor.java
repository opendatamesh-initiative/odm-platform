package org.opendatamesh.platform.pp.registry.core;

import java.io.File;
import java.net.URI;

import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class DataProductDescriptor {

    private DataProductDescriptorSource source;
    
    //private String rawContent;
    private DataProductVersionResource parsedContent;

    //private String targetURL;

    public DataProductDescriptor(DataProductDescriptorSource descriptorSource) {
        setSource(descriptorSource);
    }

   
    public static void main(String[] args) throws Exception {
        URI ROOT_DOC_LOACAL_URI = new File("src/test/resources/demo/tripexecution/data-product-descriptor.json")
                .toURI();
        URI ROOT_DOC_REMOTE_URI = new URI(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#zozzo?pippo=/xxx");

        DataProductDescriptorSource descriptorSource = new DataProductDescriptorSource(ROOT_DOC_REMOTE_URI);
        DataProductDescriptorBuilder descriptorBuilder = 
                new DataProductDescriptorBuilder(descriptorSource , "http://localhost:80/");
           
        DataProductDescriptor descriptor = null;
        descriptor = descriptorBuilder.build(true);
        
       
        //String rawContent = descriptor.getParsedContent().getComponentRawContentOrdered(false);
        DataProductVersionMapper mapper = DataProductVersionMapper.getMapper();
        String rawContent = mapper.getRawContent(descriptor.getParsedContent(), false);
        JsonNode jsonNode = mapper.readTree(rawContent);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        System.out.println(json);
    }
}
