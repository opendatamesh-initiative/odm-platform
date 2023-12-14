package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.api.asyncapi.AsyncApiParser;
import org.opendatamesh.platform.core.dpds.api.dsapi.DataStoreApiParser;
import org.opendatamesh.platform.core.dpds.api.openapi.OpenApiParser;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.ApiDefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ApiDefinitionsProcessor implements PropertiesProcessor {

    ParseContext context;

    private static final Logger logger = LoggerFactory.getLogger(ApiDefinitionsProcessor.class);

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

            ObjectNode apiNode = null;
            try {
                apiNode = (ObjectNode)context.getMapper().readTree(port.getPromises().getApi().getRawContent());
            } catch (JsonProcessingException e) {
                throw new DeserializationException("Impossible to parse raw content of port [" + port.getName() + "]", e);
            }

        
            try {     
                parseApiDefinition(port);
            } catch (FetchException e) {
                throw new DeserializationException(
                        "Impossible to parse api definition of port [" + port.getName() + "]", e);
            }
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
            parsedApiDefinition.setRawContent(apiDefinition.getRawContent());
            api.setDefinition(parsedApiDefinition);
        }
    }

    public static void process(ParseContext context) throws UnresolvableReferenceException, DeserializationException {
        ApiDefinitionsProcessor processor = new ApiDefinitionsProcessor(context);
        processor.process();
    }
}
