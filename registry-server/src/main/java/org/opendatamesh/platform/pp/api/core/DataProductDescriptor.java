package org.opendatamesh.platform.pp.api.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.opendatamesh.platform.pp.api.exceptions.core.ParseException;
import org.opendatamesh.platform.pp.api.exceptions.core.UnresolvableReferenceException;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.*;
import org.opendatamesh.platform.pp.api.resources.v1.mappers.BuildInfoResourceDeserializer;
import org.opendatamesh.platform.pp.api.resources.v1.mappers.DeployInfoResourceDeserializer;
import org.opendatamesh.platform.pp.api.resources.v1.mappers.ProvisionInfoResourceDeserializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.ValidationMessage;

import lombok.Data;

@Data
public class DataProductDescriptor {

    private String targetURL;
    private URI rootDocURI;
    private URI rootDocBaseURI;
    private String rootDocFileName;

    private String rawContent;
    private DataProductVersionResource parsedContent;

    ObjectMapper objectMapper;

    public DataProductDescriptor(URI rootDocURI) {
        setRootDocURI(rootDocURI);
    }

    public DataProductDescriptor(String content) {
        setRawContent(content);
    }

    public void setRootDocURI(String path) throws URISyntaxException {
        rootDocURI = new URI(path);
    }

    public void setRootDocURI(URI uri) {
        rootDocURI = uri.normalize();
        String path = rootDocURI.getPath();
        String scheme = rootDocURI.getScheme();
        String basePath = path.substring(0, path.lastIndexOf('/') + 1);
        try {
            rootDocBaseURI = new URI(scheme + ":" + basePath);
        } catch (URISyntaxException e) {
            throw new RuntimeException("An unexpected exception occured while creating base uri [" + scheme + ":"
                    + basePath + "] of uri [" + uri.toString() + "]", e);
        }
        rootDocFileName = path.substring(path.lastIndexOf('/') + 1);
    }

    public String loadContent() throws IOException {
        rawContent = fetchURI(rootDocURI);
        return rawContent;
    }

    public DataProductVersionResource parseContent() throws ParseException {
        if(parsedContent != null)
            return parsedContent;
        try {
            parsedContent = objectMapper.readValue(rawContent, DataProductVersionResource.class);
            // TODO verificare se si può evitare di rifare il parsing due volte. Sposterei
            // inoltre il codice
            // che estrae il raw content qui lasciandopoi solo i metodi set sulla risorsa
            parsedContent.setRawContent(rawContent);
        } catch (Exception e) {
            throw new ParseException("Impossible to parse descriptor document", e);
        }

        return parsedContent;
    }

    public void addReadOnlyProperties() throws ParseException, JsonMappingException, JsonProcessingException {
       
        String fqn, uuid;

        parseContent();

        String content = parsedContent.getRawContent();

        Map<String, Map> rootEntityProperties = objectMapper.readValue(content, HashMap.class);
        Map infoObjectProperties = rootEntityProperties.get("info");
        
        parsedContent.getInfo().setEntityType(EntityType.dataproduct.toString()); 
        infoObjectProperties.put("entityType", EntityType.dataproduct.toString());

        fqn = parsedContent.getInfo().getFullyQualifiedName();
        uuid = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
        parsedContent.getInfo().setDataProductId(uuid);
        infoObjectProperties.put("id", uuid);

        rootEntityProperties.put("info", infoObjectProperties);
        parsedContent.setRawContent(objectMapper.writeValueAsString(rootEntityProperties));

        addReadOnlyProperties(parsedContent.getInterfaceComponents().getInputPorts(), EntityType.inputport);
        addReadOnlyProperties(parsedContent.getInterfaceComponents().getOutputPorts(), EntityType.outputport);
        addReadOnlyProperties(parsedContent.getInterfaceComponents().getDiscoveryPorts(), EntityType.discoveryport);
        addReadOnlyProperties(parsedContent.getInterfaceComponents().getObservabilityPorts(), EntityType.observabilityport);
        addReadOnlyProperties(parsedContent.getInterfaceComponents().getControlPorts(), EntityType.controlport);

        addReadOnlyProperties(parsedContent.getInternalComponents().getApplicationComponents(), EntityType.application);
        addReadOnlyProperties(parsedContent.getInternalComponents().getInfrastructuralComponents(), EntityType.infrastructure);
    }

    public void addReadOnlyProperties(List<? extends ComponentResource> components, EntityType entityType) throws JsonMappingException, JsonProcessingException {
        String fqn, uuid;
        for(ComponentResource component : components) {
            Map componentProperties = objectMapper.readValue(component.getRawContent(), HashMap.class);
            
            component.setEntityType(entityType);
            componentProperties.put("entityType", entityType);
            
            fqn = (String)component.getFullyQualifiedName();
            uuid = UUID.nameUUIDFromBytes(fqn.getBytes()).toString();
            component.setId(uuid);
            componentProperties.put("id", uuid);
            
            component.setRawContent(objectMapper.writeValueAsString(componentProperties));
        }
    }

    public String getParsedContentAsString() throws ParseException {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedContent);
        } catch (JsonProcessingException e) {
            throw new ParseException("Impossible to serialize as json string the parsed content", e);
        }
    }

    public Set<ValidationMessage> validateSchema() throws JsonProcessingException {
        // TODO validate against the right schema version
        DataProductDescriptorValidator schemaValidator = new DataProductDescriptorValidator(objectMapper);
        Set<ValidationMessage> errors = schemaValidator.validateSchema(rawContent);
        return errors;
    }

    public void resolveExternalReferences() throws UnresolvableReferenceException, ParseException {
        parseContent();
        if (parsedContent.getInterfaceComponents() != null) {
            resolveExternalReferences(parsedContent.getInterfaceComponents().getOutputPorts(),
                    EntityType.outputport);
            resolveExternalReferences(parsedContent.getInterfaceComponents().getInputPorts(), EntityType.inputport);
            resolveExternalReferences(parsedContent.getInterfaceComponents().getObservabilityPorts(),
                    EntityType.observabilityport);
            resolveExternalReferences(parsedContent.getInterfaceComponents().getDiscoveryPorts(),
                    EntityType.discoveryport);
            resolveExternalReferences(parsedContent.getInterfaceComponents().getControlPorts(),
                    EntityType.controlport);
        }
        if (parsedContent.getInternalComponents() != null) {
            resolveExternalReferences(parsedContent.getInternalComponents().getApplicationComponents(),
                    EntityType.application);
            resolveExternalReferences(parsedContent.getInternalComponents().getInfrastructuralComponents(),
                    EntityType.infrastructure);
        }
    }

    private <E extends ComponentResource> void resolveExternalReferences(List<E> components,
            EntityType compoEntityType) throws UnresolvableReferenceException {
        for (int i = 0; i < components.size(); i++) {
            E component = components.get(i);
            String ref = component.getRef();
            if (ref != null && !ref.trim().startsWith("#")) {
                components.set(i, resolveExternalReference(component));
            }
        }
    }

    private <E extends ComponentResource> E resolveExternalReference(E component)
            throws UnresolvableReferenceException {
        E resolvedComponent = null;
        String ref = component.getRef();
        if (ref == null || ref.startsWith("#")) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference. Field [$ref] value [" + ref
                            + "] is not an URL to an external resource");
        }

        try {
            URI uri = new URI(ref).normalize();
            String content = fetchURI(uri);
            resolvedComponent = (E) objectMapper.readValue(content, component.getClass());
            resolvedComponent.setRawContent(content);
        } catch (Exception e) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference [" + ref + "]",
                    e);
        }
        return resolvedComponent;
    }

    public void resolveInternalReferences() throws ParseException, UnresolvableReferenceException {

        parseContent();
        if (parsedContent.getInterfaceComponents() != null) {
            resolveInternalReferences(parsedContent.getInterfaceComponents().getOutputPorts(),
                    parsedContent.getComponents(), EntityType.outputport);
            resolveInternalReferences(parsedContent.getInterfaceComponents().getInputPorts(),
                    parsedContent.getComponents(), EntityType.inputport);
            resolveInternalReferences(parsedContent.getInterfaceComponents().getObservabilityPorts(),
                    parsedContent.getComponents(), EntityType.observabilityport);
            resolveInternalReferences(parsedContent.getInterfaceComponents().getDiscoveryPorts(),
                    parsedContent.getComponents(), EntityType.discoveryport);
            resolveInternalReferences(parsedContent.getInterfaceComponents().getControlPorts(),
                    parsedContent.getComponents(), EntityType.controlport);
        }
        if (parsedContent.getInternalComponents() != null) {
            resolveInternalReferences(parsedContent.getInternalComponents().getApplicationComponents(),
                    parsedContent.getComponents(), EntityType.application);
            resolveInternalReferences(parsedContent.getInternalComponents().getInfrastructuralComponents(),
                    parsedContent.getComponents(), EntityType.infrastructure);
        }

        rawContent = getParsedContentAsString();
    }

    private <E extends ComponentResource> void resolveInternalReferences(List<E> components,
            ComponentsResource componentsObject,
            EntityType type) throws UnresolvableReferenceException {
        for (int i = 0; i < components.size(); i++) {

            E component = components.get(i);
            String ref = component.getRef();
            if (ref != null && ref.trim().startsWith("#")) {

                // internal ref example : #/components/infrastructuralComponents/eventStore
                ComponentResource resovedComponent = componentsObject.getComponentsByEntityType(type)
                        .get(ref.substring(ref.lastIndexOf("/")));

                if (resovedComponent == null) {
                    throw new UnresolvableReferenceException("Impossible to resolve internal reference [" + ref + "]");
                }

                components.set(i, (E) resovedComponent);
            }
        }
    }
    
    // Note: to be called after component resolution
    public void resolveStandardDefinitionObjects() throws JsonMappingException, JsonProcessingException, UnresolvableReferenceException {
        if (parsedContent.getInterfaceComponents() == null) {
            return;
        }

        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getInputPorts() );
        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getOutputPorts() );
        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getDiscoveryPorts() );
        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getObservabilityPorts() );
        resolveStandardDefinitionObjects( parsedContent.getInterfaceComponents().getControlPorts() );       
    }

    public void resolveStandardDefinitionObjects(List<PortResource> ports) throws JsonMappingException, JsonProcessingException, UnresolvableReferenceException {
        
        if (ports == null || ports.isEmpty()) {
            return;
        }
        for(PortResource port: ports) {
            JsonNode portObject = objectMapper.readTree(port.getRawContent());

            if(!portObject.at("/promises/api").isMissingNode()) {
                ObjectNode apiDefinitionObject = (ObjectNode)portObject.at("/promises/api/definition");
               
                String apiDefinitionRef = null, apiDefinitionContent = null;
                if(apiDefinitionObject.get("$ref") != null) { //add to DPDS inline properties to discriminate between external ref and inline object because an inline object could also have $ref as avlid property
                    String ref = apiDefinitionObject.get("$ref").asText();
                    try {
                        URI uri = new URI(ref).normalize();
                        apiDefinitionContent = fetchURI(uri);
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
                    apiDefinitionContent = objectMapper.writeValueAsString(apiDefinitionObject);
                    apiDefinitionRef = targetURL + "/definitions/{apiId}";
                    ObjectNode apiObject = (ObjectNode)portObject.at("/promises/api");
                    apiObject.remove("definition");
                    apiDefinitionObject =  apiObject.putObject("definition");
                    apiDefinitionObject.put("$ref", apiDefinitionRef);
                }

                port.getPromises().getApi().getDefinition().setRawContent(apiDefinitionContent);
                port.getPromises().getApi().getDefinition().setRef(apiDefinitionRef);
                port.setRawContent(objectMapper.writeValueAsString(portObject));
            }
        }
    }

    



    private String fetchURI(URI uri) throws IOException {

        String content = "";
        URI absoluteURI = rootDocBaseURI.resolve(uri);

        BufferedReader in;

        in = new BufferedReader(
                new InputStreamReader(absoluteURI.toURL().openStream()));
        String line;
        while ((line = in.readLine()) != null)
            content += line;
        in.close();

        return content;
    }






    // @deprecated moved to MappersConfiguration
    public static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ProvisionInfoResource.class, new ProvisionInfoResourceDeserializer());
        module.addDeserializer(BuildInfoResource.class, new BuildInfoResourceDeserializer());
        module.addDeserializer(DeployInfoResource.class, new DeployInfoResourceDeserializer());

        objectMapper.registerModule(module);
        return objectMapper;
    }

    public static void main(String[] args) throws Exception {
        URI ROOT_DOC_LOACAL_URI = new File("src/test/resources/demo/tripexecution/data-product-descriptor.json")
                .toURI();
        URI ROOT_DOC_REMOTE_URI = new URI(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#zozzo?pippo=/xxx");

        DataProductDescriptor descriptor = null;
        Set<ValidationMessage> errors = null;

        descriptor = new DataProductDescriptor(ROOT_DOC_LOACAL_URI);
        descriptor.setObjectMapper(DataProductDescriptor.buildObjectMapper());

        String json = descriptor.loadContent();
        errors = descriptor.validateSchema();

        descriptor.resolveExternalReferences();
        errors = descriptor.validateSchema();

        descriptor.resolveInternalReferences();

        // VALIDATE fqn
        // VALIDATE versions(?)// major of all components must be less or equal to the
        // major of the dp

        System.out.println(descriptor.getRawContent());
    }
}
