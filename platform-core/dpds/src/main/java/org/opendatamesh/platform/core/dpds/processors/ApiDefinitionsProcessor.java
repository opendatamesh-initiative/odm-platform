package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.api.asyncapi.AsyncApiParser;
import org.opendatamesh.platform.core.dpds.api.dsapi.DataStoreApiParser;
import org.opendatamesh.platform.core.dpds.api.openapi.OpenApiParser;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.opendatamesh.platform.core.dpds.parser.location.UriUtils;

import java.net.URI;
import java.util.List;

public class ApiDefinitionsProcessor {

    ParseContext context;
    ObjectMapper mapper;

    public ApiDefinitionsProcessor(ParseContext context) {
        this.context = context;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;

    }

    // Note: to be called after component resolution
    public void process() throws UnresolvableReferenceException, ParseException {

        DataProductVersionDPDS parsedContent = context.getResult().getDescriptorDocument();

        if (parsedContent.getInterfaceComponents() == null) {
            return;
        }

        processApiDefinitions(parsedContent.getInterfaceComponents().getInputPorts());
        processApiDefinitions(parsedContent.getInterfaceComponents().getOutputPorts());
        processApiDefinitions(parsedContent.getInterfaceComponents().getDiscoveryPorts());
        processApiDefinitions(parsedContent.getInterfaceComponents().getObservabilityPorts());
        processApiDefinitions(parsedContent.getInterfaceComponents().getControlPorts());
    }

    private void processApiDefinitions(List<PortDPDS> ports)
            throws ParseException {

        if (ports == null || ports.isEmpty()) {
            return;
        }

        for (PortDPDS port : ports) {
            ObjectNode portObject = null;
            try {
                portObject = (ObjectNode)mapper.readTree(port.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse raw content of port [" + port.getFullyQualifiedName() + "]", e);
            }

            try {
                resolveApiDefinition(port, portObject);
            } catch (UnresolvableReferenceException | FetchException e) {
                 throw new ParseException(
                        "Impossible to resolve api definition of port [" + port.getFullyQualifiedName() + "]", e);
            }
            try {     
                parseApiDefinition(port);
            } catch (FetchException e) {
                throw new ParseException(
                        "Impossible to parse api definition of port [" + port.getFullyQualifiedName() + "]", e);
            }
        }
    }

    private void resolveApiDefinition(PortDPDS port, JsonNode portObject)
            throws UnresolvableReferenceException, FetchException, ParseException {

        ObjectNode apiDefinitionNode = (ObjectNode) portObject.at("/promises/api/definition");
        if (apiDefinitionNode.isMissingNode())
            return;

        String ref = null;
        String apiDefinitionRef = null, apiDefinitionContent = null;
        if (apiDefinitionNode.get("$ref") != null) {
            ref = apiDefinitionNode.get("$ref").asText();
            
            URI uri = null, baseUri = null;
            try {
                uri = new URI(ref).normalize();
                baseUri = UriUtils.getBaseUri(new URI(port.getOriginalRef()));  
            } catch (Exception e) {
                throw new UnresolvableReferenceException(
                        "Impossible to resolve external reference [" + ref + "]",
                        e);
            }
            apiDefinitionContent = context.getLocation().fetchResource(baseUri, uri);

            apiDefinitionRef = context.getOptions().getServerUrl() + "/definitions/{apiId}";
            apiDefinitionNode.put("$ref", apiDefinitionRef);
            port.getPromises().getApi().getDefinition().setOriginalRef("ref");
        } else { // inline
            // set apiDefinitionObject as raw content of reference object
            try {
                apiDefinitionContent = mapper.writeValueAsString(apiDefinitionNode);
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible serialize api definition", e);
            }
            apiDefinitionRef = context.getOptions().getServerUrl() + "/definitions/{apiId}";
            ObjectNode apiObject = (ObjectNode) portObject.at("/promises/api");
            apiObject.remove("definition");
            apiDefinitionNode = apiObject.putObject("definition");
            apiDefinitionNode.put("$ref", apiDefinitionRef);
        }

        port.getPromises().getApi().getDefinition().setRef(apiDefinitionRef);
        port.getPromises().getApi().getDefinition().setOriginalRef(ref);
        port.getPromises().getApi().getDefinition().setRawContent(apiDefinitionContent);
        

        try {
            String rawContent = mapper.writeValueAsString(portObject);
            port.setRawContent(rawContent);
        } catch (JsonProcessingException e) {
            throw new ParseException("Impossible serialize descriptor", e);
        }
    }

    private void parseApiDefinition(PortDPDS port) throws FetchException, ParseException {

        if (port == null || port.getPromises() == null || port.getPromises().getApi() == null
                || port.getPromises().getApi().getDefinition() == null)
            return;
        
        StandardDefinitionDPDS api = port.getPromises().getApi();
        String apiDefinitionRawContent = api.getDefinition().getRawContent();
        String apiDefinitionMediaType = api.getDefinition().getMediaType();
        String specification = api.getSpecification();

        ApiDefinitionReferenceDPDS parsedApiDefinition = null;
        if ("datastoreApi".equalsIgnoreCase(specification)) {
            DataStoreApiParser dataStoreApiParser = new DataStoreApiParser(
                    context.getLocation().getRootDocumentBaseUri());
            parsedApiDefinition = dataStoreApiParser.parse(apiDefinitionRawContent, apiDefinitionMediaType);
        } else if ("asyncApi".equalsIgnoreCase(specification)) {
            AsyncApiParser asyncApiParser = new AsyncApiParser(context.getLocation().getRootDocumentBaseUri());
            parsedApiDefinition = asyncApiParser.parse(apiDefinitionRawContent, apiDefinitionMediaType);
        } else if ("openApi".equalsIgnoreCase(specification)) {
            OpenApiParser openApiParser = new OpenApiParser(context.getLocation().getRootDocumentBaseUri());
            parsedApiDefinition = openApiParser.parse(apiDefinitionRawContent, apiDefinitionMediaType);
        } else {
            System.out.println("\n\n====\n" + port.getFullyQualifiedName() + "\n====\n\n"
                    + port.getPromises().getApi().getSpecification() + " not supported");
        }

        if (parsedApiDefinition != null) {
            // we save the sub calss ApiDefinitionReferenceDPDS
            DefinitionReferenceDPDS apiDefinition = api.getDefinition();
            parsedApiDefinition.setDescription(apiDefinition.getDescription());
            parsedApiDefinition.setMediaType(apiDefinition.getMediaType());
            parsedApiDefinition.setRef(apiDefinition.getRef());
            parsedApiDefinition.setOriginalRef(apiDefinition.getOriginalRef());
            parsedApiDefinition.setRawContent(apiDefinition.getRawContent());
            api.setDefinition(parsedApiDefinition);
        }
    }

    public static void process(ParseContext context) throws UnresolvableReferenceException, ParseException {
        ApiDefinitionsProcessor processor = new ApiDefinitionsProcessor(context);
        processor.process();
    }
}
