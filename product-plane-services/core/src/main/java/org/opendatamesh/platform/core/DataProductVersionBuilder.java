package org.opendatamesh.platform.core;

import java.util.Set;

import org.opendatamesh.platform.core.exceptions.BuildException;
import org.opendatamesh.platform.core.exceptions.FetchException;
import org.opendatamesh.platform.core.exceptions.ParseException;
import org.opendatamesh.platform.core.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.exceptions.ValidationException;
import org.opendatamesh.platform.core.resolvers.ExternalReferencesResolver;
import org.opendatamesh.platform.core.resolvers.InternalReferencesResolver;
import org.opendatamesh.platform.core.resolvers.ReadOnlyPropertiesResolver;
import org.opendatamesh.platform.core.resolvers.StandardDefinitionsResolver;
import org.opendatamesh.platform.core.resolvers.TemplatesResolver;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.core.serde.DataProductVersionSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;

import lombok.Data;

@Data
public class DataProductVersionBuilder {

    DataProductVersionSource source;
    DataProductVersionResource dataProductDescriptorRes;
    
    private String targetURL;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataProductVersionBuilder.class);
    

    public DataProductVersionBuilder(DataProductVersionSource source, String serverUrl) {
        this.source = source;
        this.targetURL = serverUrl;
    }

    public DataProductVersionBuilder validateSchema() throws ParseException, ValidationException {
        Set<ValidationMessage> errors;

        // TODO validate against the right schema version
        DataProductVersionValidator schemaValidator = new DataProductVersionValidator();

        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String serailizedContent = null;

        try {
            serailizedContent = serializer.serialize(dataProductDescriptorRes, "canonical", "json", false);
        } catch (JsonProcessingException e) {
           throw new ParseException("Impossible to serialize data product version raw content", e);
        }
        errors = schemaValidator.validateSchema(serailizedContent);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Descriptor document does not comply with DPDS. The following validation errors has been found during validation [" + errors.toString() + "]", errors);
        }
        
        return this;
    }

    public DataProductVersionBuilder buildRootDoc(boolean validate) throws BuildException {
        try {
            String rawContent = source.fetchRootDoc();
            dataProductDescriptorRes = ObjectMapperFactory.getRightMapper(rawContent).readValue(rawContent, DataProductVersionResource.class);
            dataProductDescriptorRes.setRawContent(rawContent);
            if(validate) {
                validateSchema();
            }
        } catch (FetchException | ParseException | ValidationException | JsonProcessingException e) {
            throw new BuildException("Impossible to build root descriptor document",
                BuildException.Stage.LOAD_ROOT_DOC, e);
        }
        
        return this;
    }

    public DataProductVersionBuilder buildExternalReferences(boolean validate) throws BuildException {
        try {
            ExternalReferencesResolver.resolve(dataProductDescriptorRes, source);
            if(validate) {
                validateSchema();
            }
        } catch (UnresolvableReferenceException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to build external reference of root descriptor document",
                BuildException.Stage.RESOLVE_EXTERNAL_REFERENCES, e);
        } 
        return this;
    }

    public DataProductVersionBuilder buildReadOnlyProperties() throws BuildException {
        
        try {
            ReadOnlyPropertiesResolver.resolve(dataProductDescriptorRes, source);
        } catch (ParseException e) {
            throw new BuildException("Impossible to build read only properties",
                BuildException.Stage.RESOLVE_READ_ONLY_PROPERTIES, e);
        }
       
        return this;
    }

    public DataProductVersionBuilder buildTemplates() throws BuildException {
        
        try {
            TemplatesResolver.resolve(dataProductDescriptorRes, source, targetURL);
        } catch (UnresolvableReferenceException | ParseException e) {
            throw new BuildException("Impossible to build template properties",
                BuildException.Stage.RESOLVE_TEMPLATE_PROPERTIES, e);
        }
       
        return this;
    }

    

    public DataProductVersionBuilder buildStandardDefinition() throws BuildException {
              
        try {
            StandardDefinitionsResolver.resolve(dataProductDescriptorRes, source, targetURL);
        } catch (UnresolvableReferenceException | ParseException e) {
            throw new BuildException("Impossible to build standard definitions",
                BuildException.Stage.RESOLVE_STANDARD_DEFINITIONS, e);
        }
       
       
        return this;
    }
   
    public DataProductVersionBuilder buildInternalReferences(boolean validate) throws BuildException {
        try {
            InternalReferencesResolver.resolve(dataProductDescriptorRes, source);
            if(validate) {
                validateSchema();
            }
        } catch (UnresolvableReferenceException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to build internal reference of root descriptor document",
                BuildException.Stage.RESOLVE_INTERNAL_REFERENCES, e);
        } 
        return this;
    }

    public DataProductVersionResource build(boolean validate) throws BuildException  {
        buildRootDoc(true)
            .buildExternalReferences(validate)
            .buildInternalReferences(validate)
            .buildReadOnlyProperties()
            .buildStandardDefinition()
            .buildTemplates();
        return dataProductDescriptorRes;
    } 


    
}
