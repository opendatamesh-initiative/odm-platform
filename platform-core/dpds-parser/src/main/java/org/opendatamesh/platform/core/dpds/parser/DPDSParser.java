package org.opendatamesh.platform.core.dpds.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.schema.ValidationMessage;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.DataProductVersionValidator;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.*;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException.Stage;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.processors.*;

import java.util.Set;

@Data
public class DPDSParser {

    // private static final Logger logger =
    // LoggerFactory.getLogger(DPDSParser.class);

    public DPDSParser() {
    }

    public ParseResult parse(DescriptorLocation location, ParseOptions options) throws ParseException {
        try {
            location.open();
        } catch (FetchException e) {
            throw new ParseException("Impossible to open location", Stage.LOAD_ROOT_DOC, e);
        }

        ParseContext context = new ParseContext(location, options);
        parseRootDoc(context);

        if (options.isResoveExternalRef())
            processExternalReferences(context);

        /*
        In caso di reference a una porta in un subpath del root path, qui sbaglia, impedendo la risoluzione di reference,
        per API definitions; tornando indietro si perde infatti il subpath, torna nella root e non risolverà mai le
        reference per API definition, trovandosi essa nello stesso subpath in cui si trova la porta
         */
        if (options.isResoveApiDefinitions())
            processApiDefinitions(context);

        if (options.isResoveReadOnlyProperties())
            processReadOnlyProperties(context);

        if (context.getOptions().isValidate()) {
            try {
                validateSchema(context.getResult().getDescriptorDocument());
            } catch (DeserializationException | ValidationException e) {
                throw new ParseException("Parsed document is invalid",
                    ParseException.Stage.VALIDATE, e);
            }
        }

        try {
            location.close();
        } catch (FetchException e) {
            throw new ParseException("Impossible to close location", Stage.LOAD_ROOT_DOC, e);
        }

        return context.getResult();
    }

    private DPDSParser parseRootDoc(ParseContext context) throws ParseException {
        try {
            DataProductVersionDPDS descriptorDocument = null;
            String rawContent = context.getLocation().fetchRootDoc();
            context.setMapper(ObjectMapperFactory.getRightMapper(rawContent));

            DPDSDeserializer deserializer = new DPDSDeserializer();
            descriptorDocument = deserializer.deserialize(rawContent);
            context.getResult().setDescriptorDocument(descriptorDocument);
        } catch (FetchException | DeserializationException e) {
            throw new ParseException("Impossible to parse root descriptor document",
                    ParseException.Stage.LOAD_ROOT_DOC, e);
        }

        return this;
    }

    private DPDSParser processExternalReferences(ParseContext context) throws ParseException {
        try {
            ReferencesProcessor.process(context);
        } catch (UnresolvableReferenceException | DeserializationException e) {
            throw new ParseException("Impossible to resolve external reference of root descriptor document",
                    ParseException.Stage.RESOLVE_EXTERNAL_REFERENCES, e);
        }
        return this;
    }

    private DPDSParser processReadOnlyProperties(ParseContext context) throws ParseException {

        try {
            ReadOnlyPropertiesProcessor.process(context);
        } catch (DeserializationException e) {
            throw new ParseException("Impossible to process read only properties",
                    ParseException.Stage.RESOLVE_READ_ONLY_PROPERTIES, e);
        }

        return this;
    }

    private DPDSParser processApiDefinitions(ParseContext context) throws ParseException {

        try {
            ApiDefinitionsProcessor.process(context);
        } catch (UnresolvableReferenceException | DeserializationException e) {
            throw new ParseException("Impossible to process Api definitions",
                    ParseException.Stage.RESOLVE_STANDARD_DEFINITIONS, e);
        }

        return this;
    }

    public DPDSParser validateSchema(DataProductVersionDPDS descriptor) throws DeserializationException, ValidationException {
        Set<ValidationMessage> errors;

        // TODO validate against the right schema version
        DataProductVersionValidator schemaValidator = new DataProductVersionValidator();

        DPDSSerializer serializer = new DPDSSerializer("json", false);
        String serailizedContent = null;

        try {
            serailizedContent = serializer.serialize(descriptor, "canonical");
        } catch (JsonProcessingException e) {
            throw new DeserializationException("Impossible to serialize data product version raw content", e);
        }
        errors = schemaValidator.validateSchema(serailizedContent);

        if (!errors.isEmpty()) {
            throw new ValidationException(
                    "Descriptor document does not comply with DPDS. The following validation errors has been found during validation ["
                            + errors.toString() + "]",
                    errors);
        }

        return this;
    }
}
