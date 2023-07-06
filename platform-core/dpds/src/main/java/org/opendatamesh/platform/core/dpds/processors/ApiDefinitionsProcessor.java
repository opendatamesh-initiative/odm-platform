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

        processStandardDefinitionObjects(parsedContent.getInterfaceComponents().getInputPorts());
        processStandardDefinitionObjects(parsedContent.getInterfaceComponents().getOutputPorts());
        processStandardDefinitionObjects(parsedContent.getInterfaceComponents().getDiscoveryPorts());
        processStandardDefinitionObjects(parsedContent.getInterfaceComponents().getObservabilityPorts());
        processStandardDefinitionObjects(parsedContent.getInterfaceComponents().getControlPorts());
    }

    private void processStandardDefinitionObjects(List<PortDPDS> ports)
            throws UnresolvableReferenceException, ParseException {

        if (ports == null || ports.isEmpty()) {
            return;
        }
        for (PortDPDS port : ports) {
            JsonNode portObject = null;
            try {
                portObject = mapper.readTree(port.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse descriptor raw cantent", e);
            }

            if (!portObject.at("/promises/api").isMissingNode()) {
                ObjectNode apiDefinitionObject = (ObjectNode) portObject.at("/promises/api/definition");

                String apiDefinitionRef = null, apiDefinitionContent = null;
                if (apiDefinitionObject.get("$ref") != null) {
                    String ref = apiDefinitionObject.get("$ref").asText();
                    try {
                        URI uri = new URI(ref).normalize();
                        URI baseUri = UriUtils.getBaseUri(new URI(port.getOriginalRef()));
                        apiDefinitionContent = context.getLocation().fetchResource(baseUri, uri);
                    } catch (Exception e) {
                        throw new UnresolvableReferenceException(
                                "Impossible to resolve external reference [" + ref + "]",
                                e);
                    }

                    apiDefinitionRef = context.getOptions().getServerUrl() + "/definitions/{apiId}";
                    apiDefinitionObject.put("$ref", apiDefinitionRef);
                    port.getPromises().getApi().getDefinition().setOriginalRef("ref");
                } else { // inline
                    // set apiDefinitionObject as raw content of reference object
                    try {
                        apiDefinitionContent = mapper.writeValueAsString(apiDefinitionObject);
                    } catch (JsonProcessingException e) {
                        throw new ParseException("Impossible serialize api definition", e);
                    }
                    apiDefinitionRef = context.getOptions().getServerUrl() + "/definitions/{apiId}";
                    ObjectNode apiObject = (ObjectNode) portObject.at("/promises/api");
                    apiObject.remove("definition");
                    apiDefinitionObject = apiObject.putObject("definition");
                    apiDefinitionObject.put("$ref", apiDefinitionRef);
                }

                port.getPromises().getApi().getDefinition().setRawContent(apiDefinitionContent);
                port.getPromises().getApi().getDefinition().setRef(apiDefinitionRef);
                try {
                    String rawContent = mapper.writeValueAsString(portObject);
                    port.setRawContent(rawContent);
                } catch (JsonProcessingException e) {
                    throw new ParseException("Impossible serialize descriptor", e);
                }

                try {
                    processApiDefinition(port);
                } catch (FetchException e) {
                     throw new ParseException("Impossible to parse api definition of port [" + port.getFullyQualifiedName() + "]", e);
                }

            }
        }
    }

    private void processApiDefinition(PortDPDS port) throws ParseException, FetchException {
        String apiRawContent = port.getPromises().getApi().getDefinition().getRawContent();
        String mediaType = port.getPromises().getApi().getDefinition().getMediaType();
        String specification = port.getPromises().getApi().getSpecification();

        ApiDefinitionReferenceDPDS api = null;
        if ("datastoreApi".equalsIgnoreCase(specification)) {
            DataStoreApiParser dataStoreApiParser = new DataStoreApiParser(
                    context.getLocation().getRootDocumentBaseUri());
            api = dataStoreApiParser.parse(apiRawContent, mediaType);
        } else if ("asyncApi".equalsIgnoreCase(specification)) {
            AsyncApiParser asyncApiParser = new AsyncApiParser(context.getLocation().getRootDocumentBaseUri());
            api = asyncApiParser.parse(apiRawContent, mediaType);
        } else if ("openApi".equalsIgnoreCase(specification)) {
            OpenApiParser openApiParser = new OpenApiParser(context.getLocation().getRootDocumentBaseUri());
            api = openApiParser.parse(apiRawContent, mediaType);
        } else {
            System.out.println("\n\n====\n" + port.getFullyQualifiedName() + "\n====\n\n"
                    + port.getPromises().getApi().getSpecification() + " not supported");
        }

        if(api != null) {
            DefinitionReferenceDPDS definition = port.getPromises().getApi().getDefinition();
            api.setDescription(definition.getDescription());
            api.setMediaType(definition.getMediaType());
            api.setRef(definition.getRef());
            api.setOriginalRef(definition.getOriginalRef());
            api.setRawContent(definition.getRawContent());
            port.getPromises().getApi().setDefinition(api);
        }
    }

    public static void process(ParseContext context) throws UnresolvableReferenceException, ParseException {
        ApiDefinitionsProcessor processor = new ApiDefinitionsProcessor(context);
        processor.process();
    }
}
