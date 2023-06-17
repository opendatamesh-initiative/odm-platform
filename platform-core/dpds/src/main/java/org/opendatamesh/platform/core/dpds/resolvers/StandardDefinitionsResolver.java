package org.opendatamesh.platform.core.dpds.resolvers;

import java.net.URI;
import java.util.List;

import org.opendatamesh.platform.core.dpds.DataProductVersionSource;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StandardDefinitionsResolver {

    DataProductVersionDPDS dataProductVersion;
    DataProductVersionSource source;
    private String targetURL;
    ObjectMapper mapper;

    public StandardDefinitionsResolver(DataProductVersionDPDS dataProductVersionRes, DataProductVersionSource source, String targetURL) {
        this.dataProductVersion = dataProductVersionRes;
        this.source = source;
        this.targetURL = targetURL;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;
        
    }

    // Note: to be called after component resolution
    public void resolve() throws UnresolvableReferenceException, ParseException  {
        
        DataProductVersionDPDS parsedContent = dataProductVersion;

        if (parsedContent.getInterfaceComponents() == null) {
            return;
        }

        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getInputPorts() );
        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getOutputPorts() );
        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getDiscoveryPorts() );
        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getObservabilityPorts() );
        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getControlPorts() );       
    }

    private void resolveStandardDefinitionObjects(List<PortDPDS> ports) throws UnresolvableReferenceException, ParseException {
       
        if (ports == null || ports.isEmpty()) {
            return;
        }
        for(PortDPDS port: ports) {
            JsonNode portObject = null;
            try {
                portObject = mapper.readTree(port.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse descriptor raw cantent", e);
            }

            if(!portObject.at("/promises/api").isMissingNode()) {
                ObjectNode apiDefinitionObject = (ObjectNode)portObject.at("/promises/api/definition");
               
                String apiDefinitionRef = null, apiDefinitionContent = null;
                if(apiDefinitionObject.get("$ref") != null) { //add to DPDS inline properties to discriminate between external ref and inline object because an inline object could also have $ref as avlid property
                    String ref = apiDefinitionObject.get("$ref").asText();
                    try {
                        URI uri = new URI(ref).normalize();
                        apiDefinitionContent =   source.fetchResource(uri);
                    } catch (Exception e) {
                        throw new UnresolvableReferenceException(
                                "Impossible to resolve external reference [" + ref + "]",
                                e);
                    }
                    
                    apiDefinitionRef = targetURL + "/definitions/{apiId}";
                    apiDefinitionObject.put("$ref", apiDefinitionRef);
                    port.getPromises().getApi().getDefinition().setOriginalRef("ref");
                } else  { // inline
                    // set apiDefinitionObject as raw content of reference object
                    try {
                        apiDefinitionContent = mapper.writeValueAsString(apiDefinitionObject);
                    } catch (JsonProcessingException e) {
                        throw new ParseException("Impossible serialize api definition", e);
                    }
                    apiDefinitionRef = targetURL+ "/definitions/{apiId}";
                    ObjectNode apiObject = (ObjectNode)portObject.at("/promises/api");
                    apiObject.remove("definition");
                    apiDefinitionObject =  apiObject.putObject("definition");
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
                
            }
        }
    }


    public static void resolve(DataProductVersionDPDS dataProductVersionRes,  DataProductVersionSource source, String targetURL) throws UnresolvableReferenceException, ParseException  {
        StandardDefinitionsResolver resolver = new StandardDefinitionsResolver(dataProductVersionRes, source, targetURL);
        resolver.resolve();
    }
}
