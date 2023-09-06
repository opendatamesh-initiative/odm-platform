package org.opendatamesh.platform.core.dpds.processors;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.opendatamesh.platform.core.dpds.api.asyncapi.AsyncApiParser;
import org.opendatamesh.platform.core.dpds.api.dsapi.DataStoreApiParser;
import org.opendatamesh.platform.core.dpds.api.openapi.OpenApiParser;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.opendatamesh.platform.core.dpds.parser.location.UriFetcher;
import org.opendatamesh.platform.core.dpds.parser.location.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ApiDefinitionsProcessor implements PropertiesProcessor {

    ParseContext context;

    private static final Logger logger = LoggerFactory.getLogger(UriFetcher.class);

    public ApiDefinitionsProcessor(ParseContext context) {
        this.context = context;
    }

    public void process() throws UnresolvableReferenceException, DeserializationException {

        DataProductVersionDPDS parsedContent = context.getResult().getDescriptorDocument();
        Objects.requireNonNull(parsedContent, "Impossible to prcess API definitions. Descriptor document is null");

        InterfaceComponentsDPDS interfaceComponents = parsedContent.getInterfaceComponents();
        if (interfaceComponents != null) {
            if(interfaceComponents.hasPorts(EntityTypeDPDS.INPUTPORT)) {
                processApiDefinitions(interfaceComponents.getInputPorts());
            }
            
            if(interfaceComponents.hasPorts(EntityTypeDPDS.OUTPUTPORT)) {
                processApiDefinitions(interfaceComponents.getOutputPorts());
            }

            if(interfaceComponents.hasPorts(EntityTypeDPDS.DISCOVERYPORT)) {
                processApiDefinitions(interfaceComponents.getDiscoveryPorts());
            }

            if(interfaceComponents.hasPorts(EntityTypeDPDS.OBSERVABILITYPORT)) {
                processApiDefinitions(interfaceComponents.getObservabilityPorts());
            }

            if(interfaceComponents.hasPorts(EntityTypeDPDS.CONTROLPORT)) {
                processApiDefinitions(interfaceComponents.getControlPorts());
            }

            logger.info("API definitions sucesfully processed");
        } else {
            logger.info("No API definition to process");
        }       
    }

    private void processApiDefinitions(List<PortDPDS> ports)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(ports, "Parameter [ports] cannot be null");

        for (PortDPDS port : ports) {

            if(port.hasApiDefinition() == false) {
                logger.debug("No API definition to process for port [" + port.getName()  + "]");
                continue;
            }

            String ref = port.getPromises().getApi().getDefinition().getRef();
            if(ref != null && ref.startsWith(context.getOptions().getServerUrl())) {
                logger.debug("API definition for port [" + port.getName()  + "] has been already processed");
                continue;
            }

            ObjectNode apiNode = null;
            try {
                apiNode = (ObjectNode)context.getMapper().readTree(port.getPromises().getApi().getRawContent());
            } catch (JsonProcessingException e) {
                throw new DeserializationException("Impossible to parse raw content of port [" + port.getName() + "]", e);
            }

            try {
                resolveApiDefinition(port, apiNode);
            } catch (UnresolvableReferenceException | FetchException e) {
                 throw new UnresolvableReferenceException(
                        "Impossible to resolve api definition of port [" + port.getName() + "]", e);
            }
            try {     
                parseApiDefinition(port);
            } catch (FetchException e) {
                throw new DeserializationException(
                        "Impossible to parse api definition of port [" + port.getName() + "]", e);
            }
        }
    }

    private void resolveApiDefinition(PortDPDS portResource, ObjectNode apiNode)
            throws UnresolvableReferenceException, FetchException, DeserializationException {

        if(portResource.hasApiDefinition() == false) return;
        ObjectNode apiDefinitionNode = (ObjectNode) apiNode.get("definition");
        if (apiDefinitionNode.isMissingNode())
            return;

        String ref = null;
        String apiDefinitionRef = null, apiDefinitionContent = null;
        if (apiDefinitionNode.get("$ref") != null) {
            ref = apiDefinitionNode.get("$ref").asText();
            
            URI uri = null, baseUri = null;
            try {
                uri = new URI(ref).normalize();
                if(portResource.getOriginalRef() != null) {
                    baseUri = UriUtils.getBaseUri(new URI(portResource.getOriginalRef())); 
                } else {
                    baseUri = context.getLocation().getRootDocumentBaseUri();
                }
                 
            } catch (Exception e) {
                throw new UnresolvableReferenceException(
                        "Impossible to resolve external reference [" + ref + "]",
                        e);
            }
            apiDefinitionContent = context.getLocation().fetchResource(baseUri, uri);

            apiDefinitionRef = context.getOptions().getServerUrl() + "/apis/{apiId}";
            apiDefinitionNode.put("$ref", apiDefinitionRef);
            portResource.getPromises().getApi().getDefinition().setOriginalRef(ref);
        } else { // inline
            // set apiDefinitionObject as raw content of reference object
            try {
                apiDefinitionContent = context.getMapper().writeValueAsString(apiDefinitionNode);
            } catch (JsonProcessingException e) {
                throw new DeserializationException("Impossible serialize api definition", e);
            }
            apiDefinitionRef = context.getOptions().getServerUrl() + "/apis/{apiId}";
            
            apiNode.remove("definition");
            apiDefinitionNode = apiNode.putObject("definition");
            apiDefinitionNode.put("$ref", apiDefinitionRef);
        }

        portResource.getPromises().getApi().getDefinition().setRef(apiDefinitionRef);
        portResource.getPromises().getApi().getDefinition().setOriginalRef(ref);
        portResource.getPromises().getApi().getDefinition().setRawContent(apiDefinitionContent);
        

        try {
            String apiContent = context.getMapper().writeValueAsString(apiNode);
            portResource.getPromises().getApi().setRawContent(apiContent);
        } catch (JsonProcessingException e) {
            throw new DeserializationException("Impossible serialize descriptor", e);
        }
    }

    private void parseApiDefinition(PortDPDS port) throws FetchException, DeserializationException {

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
            //parsedApiDefinition = new ApiDefinitionReferenceDPDS();
        }

        if (parsedApiDefinition != null) {
            // we save the sub class ApiDefinitionReferenceDPDS
            DefinitionReferenceDPDS apiDefinition = api.getDefinition();
            parsedApiDefinition.setDescription(apiDefinition.getDescription());
            parsedApiDefinition.setMediaType(apiDefinition.getMediaType());
            parsedApiDefinition.setRef(apiDefinition.getRef());
            parsedApiDefinition.setOriginalRef(apiDefinition.getOriginalRef());
            parsedApiDefinition.setRawContent(apiDefinition.getRawContent());
            api.setDefinition(parsedApiDefinition);
        }
    }

    public static void process(ParseContext context) throws UnresolvableReferenceException, DeserializationException {
        ApiDefinitionsProcessor processor = new ApiDefinitionsProcessor(context);
        processor.process();
    }
}
