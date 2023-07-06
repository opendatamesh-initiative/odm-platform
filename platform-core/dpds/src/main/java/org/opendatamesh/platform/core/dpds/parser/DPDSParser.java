package org.opendatamesh.platform.core.dpds.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.schema.ValidationMessage;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.DataProductVersionValidator;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.*;
import org.opendatamesh.platform.core.dpds.exceptions.BuildException.Stage;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.processors.*;
import org.opendatamesh.platform.core.dpds.serde.DataProductVersionSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Data
public class DPDSParser {

   
    private static final Logger logger = LoggerFactory.getLogger(DPDSParser.class);
    

    public DPDSParser() {        
    }

    public ParseResult parse(DescriptorLocation location, ParseOptions options) throws BuildException  {
        try {
            location.open();
        } catch (FetchException e) {
           throw new BuildException("Impossible to open location", Stage.LOAD_ROOT_DOC, e);
        }

        ParseContext context = new ParseContext(location, options);
        parseRootDoc(context);
        if(options.isResoveExternalRef()) processExternalReferences(context);
        if(options.isResoveInternalRef()) processInternalReferences(context);
        if(options.isResoveReadOnlyProperties()) processReadOnlyProperties(context);
        if(options.isResoveStandardDefinitions()) processStandardDefinitions(context);
        if(options.isResoveTemplates()) processTemplates(context);
        
        try {
            location.close();
        } catch (FetchException e) {
           throw new BuildException("Impossible to close location", Stage.LOAD_ROOT_DOC, e);
        }

        return context.getResult();
    } 

    private DPDSParser parseRootDoc(ParseContext context) throws BuildException {
        try {
            DataProductVersionDPDS descriptorDocument = null;
            String rawContent = context.getLocation().fetchRootDoc();
            try {
                descriptorDocument = ObjectMapperFactory.getRightMapper(rawContent).readValue(rawContent, DataProductVersionDPDS.class);
            } catch (JsonProcessingException e) {
                throw new ParseException("Root document format is not valid", e);
            }
            descriptorDocument.setRawContent(rawContent);
            if(context.getOptions().isValidateRootDocumet()) {
                validateSchema(descriptorDocument);
            }
            context.getResult().setDescriptorDocument(descriptorDocument);
        } catch (FetchException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to parse root descriptor document",
                BuildException.Stage.LOAD_ROOT_DOC, e);
        }
        
        return this;
    }

    private DPDSParser processExternalReferences(ParseContext context) throws BuildException {
        try {
            ExternalReferencesProcessor.process(context);
            if(context.getOptions().isValidateExternalRefs()) {
                validateSchema(context.getResult().getDescriptorDocument());
            }
        } catch (UnresolvableReferenceException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to build external reference of root descriptor document",
                BuildException.Stage.RESOLVE_EXTERNAL_REFERENCES, e);
        } 
        return this;
    }

    private DPDSParser processReadOnlyProperties(ParseContext context) throws BuildException {
        
        try {
            ReadOnlyPropertiesProcessor.process(context);
        } catch (ParseException e) {
            throw new BuildException("Impossible to build read only properties",
                BuildException.Stage.RESOLVE_READ_ONLY_PROPERTIES, e);
        }
       
        return this;
    }

    private DPDSParser processTemplates(ParseContext context) throws BuildException {
        
        try {
            TemplatesResolver.resolve(context);
        } catch (UnresolvableReferenceException | ParseException e) {
            throw new BuildException("Impossible to build template properties",
                BuildException.Stage.RESOLVE_TEMPLATE_PROPERTIES, e);
        }
       
        return this;
    }

    

    private DPDSParser processStandardDefinitions(ParseContext context) throws BuildException {
              
        try {
            ApiDefinitionsProcessor.process(context);
        } catch (UnresolvableReferenceException | ParseException e) {
            throw new BuildException("Impossible to build standard definitions",
                BuildException.Stage.RESOLVE_STANDARD_DEFINITIONS, e);
        }
       
       
        return this;
    }
   
    private DPDSParser processInternalReferences(ParseContext context) throws BuildException {
        try {
            InternalReferencesProcessor.process(context);
            if(context.getOptions().isValidateInternalRefs()) {
                validateSchema(context.getResult().getDescriptorDocument());
            }
        } catch (UnresolvableReferenceException | ParseException | ValidationException e) {
            throw new BuildException("Impossible to build internal reference of root descriptor document",
                BuildException.Stage.RESOLVE_INTERNAL_REFERENCES, e);
        } 
        return this;
    }   

    public DPDSParser validateSchema(DataProductVersionDPDS descriptor) throws ParseException, ValidationException {
        Set<ValidationMessage> errors;

        // TODO validate against the right schema version
        DataProductVersionValidator schemaValidator = new DataProductVersionValidator();

        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String serailizedContent = null;

        try {
            serailizedContent = serializer.serialize(descriptor, "canonical", "json", false);
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
