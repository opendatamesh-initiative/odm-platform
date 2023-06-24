package org.opendatamesh.platform.core.dpds;

import java.util.Set;

import org.opendatamesh.platform.core.dpds.exceptions.BuildException;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.exceptions.ValidationException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.processors.ExternalReferencesProcessor;
import org.opendatamesh.platform.core.dpds.processors.InternalReferencesProcessor;
import org.opendatamesh.platform.core.dpds.processors.ReadOnlyPropertiesProcessor;
import org.opendatamesh.platform.core.dpds.processors.ApiDefinitionsProcessor;
import org.opendatamesh.platform.core.dpds.processors.TemplatesResolver;
import org.opendatamesh.platform.core.dpds.serde.DataProductVersionSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.schema.ValidationMessage;

import lombok.Data;

@Data
public class DPDSParser {

    DataProductVersionSource source;
    DataProductVersionDPDS dataProductVersion;
    
    private String targetURL;

    private static final Logger LOGGER = LoggerFactory.getLogger(DPDSParser.class);
    

    public DPDSParser(DataProductVersionSource source, String serverUrl) {
        this.source = source;
        this.targetURL = serverUrl;
    }

    public DataProductVersionDPDS parse(boolean validate) throws BuildException  {
        parseRootDoc(true)
            .processExternalReferences(validate)
            .processInternalReferences(validate)
            .processReadOnlyProperties()
            .processStandardDefinition()
            .processTemplates();
        return dataProductVersion;
    } 

    public DPDSParser parseRootDoc(boolean validate) throws BuildException {
        try {
            String rawContent = source.fetchRootDoc();
            dataProductVersion = ObjectMapperFactory.getRightMapper(rawContent).readValue(rawContent, DataProductVersionDPDS.class);
            dataProductVersion.setRawContent(rawContent);
            if(validate) {
                validateSchema();
            }
        } catch (FetchException | ParseException | ValidationException | JsonProcessingException e) {
            throw new BuildException("Impossible to build root descriptor document",
                BuildException.Stage.LOAD_ROOT_DOC, e);
        }
        
        return this;
    }

    public DPDSParser processExternalReferences(boolean validate) throws BuildException {
        try {
            ExternalReferencesProcessor.process(dataProductVersion, source);
            if(validate) {
                validateSchema();
            }
        } catch (UnresolvableReferenceException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to build external reference of root descriptor document",
                BuildException.Stage.RESOLVE_EXTERNAL_REFERENCES, e);
        } 
        return this;
    }

    public DPDSParser processReadOnlyProperties() throws BuildException {
        
        try {
            ReadOnlyPropertiesProcessor.process(dataProductVersion, source);
        } catch (ParseException e) {
            throw new BuildException("Impossible to build read only properties",
                BuildException.Stage.RESOLVE_READ_ONLY_PROPERTIES, e);
        }
       
        return this;
    }

    public DPDSParser processTemplates() throws BuildException {
        
        try {
            TemplatesResolver.resolve(dataProductVersion, source, targetURL);
        } catch (UnresolvableReferenceException | ParseException e) {
            throw new BuildException("Impossible to build template properties",
                BuildException.Stage.RESOLVE_TEMPLATE_PROPERTIES, e);
        }
       
        return this;
    }

    

    public DPDSParser processStandardDefinition() throws BuildException {
              
        try {
            ApiDefinitionsProcessor.process(dataProductVersion, source, targetURL);
        } catch (UnresolvableReferenceException | ParseException e) {
            throw new BuildException("Impossible to build standard definitions",
                BuildException.Stage.RESOLVE_STANDARD_DEFINITIONS, e);
        }
       
       
        return this;
    }
   
    public DPDSParser processInternalReferences(boolean validate) throws BuildException {
        try {
            InternalReferencesProcessor.process(dataProductVersion, source);
            if(validate) {
                validateSchema();
            }
        } catch (UnresolvableReferenceException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to build internal reference of root descriptor document",
                BuildException.Stage.RESOLVE_INTERNAL_REFERENCES, e);
        } 
        return this;
    }   

    public DPDSParser validateSchema() throws ParseException, ValidationException {
        Set<ValidationMessage> errors;

        // TODO validate against the right schema version
        DataProductVersionValidator schemaValidator = new DataProductVersionValidator();

        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String serailizedContent = null;

        try {
            serailizedContent = serializer.serialize(dataProductVersion, "canonical", "json", false);
        } catch (JsonProcessingException e) {
           throw new ParseException("Impossible to serialize data product version raw content", e);
        }
        errors = schemaValidator.validateSchema(serailizedContent);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Descriptor document does not comply with DPDS. The following validation errors has been found during validation [" + errors.toString() + "]", errors);
        }
        
        return this;
    }
}
