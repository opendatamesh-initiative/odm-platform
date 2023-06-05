package org.opendatamesh.platform.pp.registry.core;

import java.util.Set;

import org.opendatamesh.platform.pp.registry.core.exceptions.BuildException;
import org.opendatamesh.platform.pp.registry.core.exceptions.FetchException;
import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.core.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.pp.registry.core.exceptions.ValidationException;
import org.opendatamesh.platform.pp.registry.core.resolvers.ExternalReferencesResolver;
import org.opendatamesh.platform.pp.registry.core.resolvers.InternalReferencesResolver;
import org.opendatamesh.platform.pp.registry.core.resolvers.ReadOnlyPropertiesResolver;
import org.opendatamesh.platform.pp.registry.core.resolvers.StandardDefinitionsResolver;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.schema.ValidationMessage;

import lombok.Data;

@Data
public class DataProductDescriptorBuilder {

    DataProductDescriptor descriptor;
    DataProductVersionMapper mapper;
    
    private String targetURL;

    public DataProductDescriptorBuilder(DataProductDescriptorSource source, String serverUrl) {
        descriptor = new DataProductDescriptor(source);
        setTargetURL(serverUrl);
        mapper = DataProductVersionMapper.getMapper();
    }

    public DataProductDescriptorBuilder validateSchema() throws ParseException, ValidationException {
        Set<ValidationMessage> errors;

        // TODO validate against the right schema version
        DataProductDescriptorValidator schemaValidator = new DataProductDescriptorValidator();
        errors = schemaValidator.validateSchema(descriptor.getParsedContent().getRawContent(false));
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Descriptor document does not comply with DPDS. The following validation errors has been found during validation [" + errors.toString() + "]", errors);
        }
        
        return this;
    }

    public DataProductDescriptorBuilder buildRootDoc(boolean validate) throws BuildException {
        try {
            String rawContent = descriptor.getSource().fetchRootDoc();
            DataProductVersionResource parsedContent = mapper.readValue(rawContent, DataProductVersionResource.class);
            parsedContent.setRawContent(rawContent);
            descriptor.setParsedContent(parsedContent);;
            if(validate) {
                validateSchema();
            }
        } catch (FetchException | ParseException | ValidationException | JsonProcessingException e) {
            throw new BuildException("Impossible to build root descriptor document",
                BuildException.Stage.LOAD_ROOT_DOC, e);
        }
        
        return this;
    }

    public DataProductDescriptorBuilder buildExternalReferences(boolean validate) throws BuildException {
        try {
            ExternalReferencesResolver.resolve(descriptor);
            if(validate) {
                validateSchema();
            }
        } catch (UnresolvableReferenceException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to build external reference of root descriptor document",
                BuildException.Stage.RESOLVE_EXTERNAL_REFERENCES, e);
        } 
        return this;
    }

    public DataProductDescriptorBuilder buildReadOnlyProperties() throws BuildException {
        
        try {
            ReadOnlyPropertiesResolver.resolve(descriptor);
        } catch (ParseException e) {
            throw new BuildException("Impossible to build read only properties",
                BuildException.Stage.RESOLVE_READ_ONLY_PROPERTIES, e);
        }
       
        return this;
    }

    public DataProductDescriptorBuilder buildStandardDefinition() throws BuildException {
              
        try {
            StandardDefinitionsResolver.resolve(descriptor, targetURL);
        } catch (UnresolvableReferenceException | ParseException e) {
            throw new BuildException("Impossible to build standard definitions",
                BuildException.Stage.RESOLVE_STANDARD_DEFINITIONS, e);
        }
       
       
        return this;
    }
   
    public DataProductDescriptorBuilder buildInternalReferences(boolean validate) throws BuildException {
        try {
            InternalReferencesResolver.resolve(descriptor);
            if(validate) {
                validateSchema();
            }
        } catch (UnresolvableReferenceException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to build internal reference of root descriptor document",
                BuildException.Stage.RESOLVE_INTERNAL_REFERENCES, e);
        } 
        return this;
    }

    public DataProductDescriptor build(boolean validate) throws BuildException  {
        buildRootDoc(true)
            .buildExternalReferences(validate)
            .buildInternalReferences(validate)
            .buildReadOnlyProperties()
            .buildStandardDefinition();
        return descriptor;
    }

    
}
