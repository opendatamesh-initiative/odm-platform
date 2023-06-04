package org.opendatamesh.platform.pp.registry.core;

import java.io.File;
import java.net.URI;
import java.util.Set;

import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.core.resolvers.ExternalReferencesResolver;
import org.opendatamesh.platform.pp.registry.core.resolvers.InternalReferencesResolver;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.BuildInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DeployInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ProvisionInfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.BuildInfoResourceDeserializer;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DeployInfoResourceDeserializer;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.ProvisionInfoResourceDeserializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.networknt.schema.ValidationMessage;

import lombok.Data;

@Data
public class DataProductDescriptor {

    private DataProductDescriptorSource source;
    private String targetURL;
    private String rawContent;
    private DataProductVersionResource parsedContent;

    ObjectMapper objectMapper;

    public DataProductDescriptor(DataProductDescriptorSource descriptorSource) {
        setSource(descriptorSource);
    }

    public DataProductVersionResource parseContent() throws ParseException {
        if(parsedContent != null)
            return parsedContent;
        try {
            parsedContent = objectMapper.readValue(rawContent, DataProductVersionResource.class);
            parsedContent.setRawContent(rawContent);
        } catch (Exception e) {
            throw new ParseException("Impossible to parse descriptor document", e);
        }

        return parsedContent;
    }

    public String getParsedContentAsString() throws ParseException {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedContent);
        } catch (JsonProcessingException e) {
            throw new ParseException("Impossible to serialize as json string the parsed content", e);
        }
    }

    public Set<ValidationMessage> validateSchema() throws ParseException {
        // TODO validate against the right schema version
        DataProductDescriptorValidator schemaValidator = new DataProductDescriptorValidator(objectMapper);
        Set<ValidationMessage> errors = schemaValidator.validateSchema(rawContent);
        return errors;
    }

    // @deprecated moved to MappersConfiguration
    public static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ProvisionInfoResource.class, new ProvisionInfoResourceDeserializer());
        module.addDeserializer(BuildInfoResource.class, new BuildInfoResourceDeserializer());
        module.addDeserializer(DeployInfoResource.class, new DeployInfoResourceDeserializer());

        objectMapper.registerModule(module);
        return objectMapper;
    }

    public static void main(String[] args) throws Exception {
        URI ROOT_DOC_LOACAL_URI = new File("src/test/resources/demo/tripexecution/data-product-descriptor.json")
                .toURI();
        URI ROOT_DOC_REMOTE_URI = new URI(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#zozzo?pippo=/xxx");

        DataProductDescriptorSource descriptorSource = new DataProductDescriptorSource(ROOT_DOC_REMOTE_URI);
        DataProductDescriptorBuilder descriptorBuilder = 
                new DataProductDescriptorBuilder(descriptorSource, buildObjectMapper() , "http://localhost:80/");
           
        DataProductDescriptor descriptor = null;
        descriptor = descriptorBuilder.build(true);
        
        /*
        DataProductDescriptor descriptor = null;
        Set<ValidationMessage> errors = null;

        DataProductDescriptorSource descriptorSource = 
            new DataProductDescriptorSource(ROOT_DOC_REMOTE_URI);
        
        descriptor = new DataProductDescriptor(descriptorSource);
       

        errors = descriptor.validateSchema();

        ExternalReferencesResolver.resolve(descriptor);
        errors = descriptor.validateSchema();

        InternalReferencesResolver.resolve(descriptor);
        // VALIDATE fqn
        // VALIDATE versions(?)// major of all components must be less or equal to the
        // major of the dp

        */
        System.out.println(descriptor.getParsedContent().getComponentRawContent(false));
    }
}
