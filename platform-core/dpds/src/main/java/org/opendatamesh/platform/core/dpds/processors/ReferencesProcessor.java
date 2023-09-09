package org.opendatamesh.platform.core.dpds.processors;

import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;

import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.ComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.InternalComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSDeserializer;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.opendatamesh.platform.core.dpds.parser.location.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReferencesProcessor implements PropertiesProcessor {

    ParseContext context;

    private static final Logger logger = LoggerFactory.getLogger(ReferencesProcessor.class);

    public ReferencesProcessor(ParseContext context) {
        this.context = context;
    }

    @Override
    public void process() throws UnresolvableReferenceException, DeserializationException {

        DataProductVersionDPDS descriptorResource = context.getResult().getDescriptorDocument();

        if (descriptorResource.hasInterfaceComponents()) {
            resolveInterfaceComponents(descriptorResource.getInterfaceComponents());
        }
        if (descriptorResource.hasInternalComponents()) {
            resolveInternalComponents(descriptorResource.getInternalComponents());
        }
    }

    private void resolveInterfaceComponents(InterfaceComponentsDPDS interfaceComponents)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(interfaceComponents, "Input parameter [interfaceComponents] cannot be null");

        for (EntityTypeDPDS type : EntityTypeDPDS.PORTS) {
            List<PortDPDS> ports = interfaceComponents.getPortListByEntityType(type);
            if (ports != null) {
                resolveComponents(ports);
            }

        }
    }

    private void resolveInternalComponents(InternalComponentsDPDS internalComponents)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(internalComponents, "Input parameter [internalComponents] cannot be null");

        if (internalComponents.getApplicationComponents() != null) {
            resolveComponents(internalComponents.getApplicationComponents());
        }

        if (internalComponents.getInfrastructuralComponents() != null) {
            resolveComponents(internalComponents.getInfrastructuralComponents());
        }

        if (internalComponents.hasLifecycleInfo()) {
            resolveTemplateComponents(internalComponents.getLifecycleInfo());
        }

    }

    private void resolveTemplateComponents(LifecycleInfoDPDS lifecycleResource)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(lifecycleResource, "Input parameter [lifecycleResource] cannot be null");

        List<LifecycleActivityInfoDPDS> acivityResources = lifecycleResource.getActivityInfos();

        for (LifecycleActivityInfoDPDS acivityResource : acivityResources) {
            if (acivityResource.hasTemplate() == false)
                continue;

            StandardDefinitionDPDS templateResource = acivityResource.getTemplate();
           
            templateResource = resolveComponent(templateResource);
            acivityResource.setTemplate(templateResource);
            

            URI templateBaseUri = null;
            if (templateResource.getOriginalRef() != null) {
                try {
                    URI portUri = new URI(templateResource.getOriginalRef());
                    templateBaseUri = UriUtils.getBaseUri(portUri);
                } catch (URISyntaxException e) {
                    throw new UnresolvableReferenceException(
                            "Impossible to resolve external reference [" + templateResource.getOriginalRef() + "]",
                            e);
                }
            } else {
                templateBaseUri = context.getLocation().getRootDocumentBaseUri();;
            }
           
            if (templateResource.getDefinition() != null) {
                resolveDefinition(templateResource, templateBaseUri);
            }
            

        }
    }

    private <E extends ComponentDPDS> void resolveComponents(List<E> components)
            throws UnresolvableReferenceException, DeserializationException {

        URI baseUri = context.getLocation().getRootDocumentBaseUri();
        resolveComponents(components, baseUri);
    }

    private <E extends ComponentDPDS> void resolveComponents(List<E> components, URI baseURI)
            throws UnresolvableReferenceException, DeserializationException {

        for (int i = 0; i < components.size(); i++) {
            E component = null;

            component = components.get(i);
            component = resolveComponent(component, baseURI);
            components.set(i, component);
        }
    }

    private <E extends ComponentDPDS> E resolveComponent(E component)
            throws UnresolvableReferenceException, DeserializationException {
        URI baseUri = context.getLocation().getRootDocumentBaseUri();
        return resolveComponent(component, baseUri);
    }

    private <E extends ComponentDPDS> E resolveComponent(E component, URI baseUri)
            throws UnresolvableReferenceException, DeserializationException {

        URI componentBaseUri = null;
        if (component.isExternalReference()) {
            component = resolveComponentFromExternalRef(component, baseUri);
            componentBaseUri = baseUri;
        } else if (component.isInternalReference()) {
            component = resolveComponentFromInternalRef(component);
            componentBaseUri = context.getLocation().getRootDocumentBaseUri();
        }

        if (component.getOriginalRef() != null) {
            try {
                URI portUri = new URI(component.getOriginalRef());
                componentBaseUri = UriUtils.getBaseUri(portUri);
            } catch (URISyntaxException e) {
                throw new UnresolvableReferenceException(
                        "Impossible to resolve external reference [" + component.getOriginalRef() + "]",
                        e);
            }
        } 

        if (component instanceof PortDPDS) {
            PortDPDS port = (PortDPDS)component;
            if(port.hasApi()) {
                StandardDefinitionDPDS api = resolveComponent(port.getPromises().getApi(), componentBaseUri);
                port.getPromises().setApi(api);
            }
        }

        
        if (component instanceof StandardDefinitionDPDS) {
            StandardDefinitionDPDS stdDef = (StandardDefinitionDPDS)component;
            if (stdDef.getDefinition() != null) {
                resolveDefinition(stdDef, componentBaseUri);
            }
        }


        return component;
    }

    @SuppressWarnings("unchecked")
    private <E extends ComponentDPDS> E resolveComponentFromExternalRef(E component, URI baseURI)
            throws UnresolvableReferenceException {

        E resolvedComponent = null;

        try {
            URI uri = new URI(component.getRef()).normalize();
            String contentContent = context.getLocation().fetchResource(baseURI, uri);

            DPDSDeserializer deserializer = new DPDSDeserializer();
            resolvedComponent = (E) deserializer.deserializeComponent(contentContent, component.getClass());
            resolvedComponent.setRawContent(contentContent);
            resolvedComponent.setOriginalRef(baseURI.resolve(uri).toString());
        } catch (Throwable t) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference [" + component.getRef() + "]",
                    t);
        }

        return resolvedComponent;
    }

    private <E extends ComponentDPDS> E resolveComponentFromInternalRef(E component)
            throws UnresolvableReferenceException {

        E resolvedComponent = null;

        if (component.isInternalReference()) {
            DataProductVersionDPDS descriptorResource = context.getResult().getDescriptorDocument();
            ComponentsDPDS componentsResource = descriptorResource.getComponents();
            EntityTypeDPDS type = EntityTypeDPDS.resolveGroupingPropertyName(component.getInternalReferenceGroupName());
            Map<String, ComponentDPDS> sharedComponents = componentsResource.getComponentsByEntityType(type);
            resolvedComponent = (E) sharedComponents.get(component.getInternalReferenceComponentName());
            if (resolvedComponent == null) {
                throw new UnresolvableReferenceException(
                        "Impossible to resolve internal reference [" + component.getRef() + "]");
            }
        } else { // nothinh to do
            resolvedComponent = component;
        }
        return resolvedComponent;
    }

    private void resolveDefinition(StandardDefinitionDPDS standardDefinitionResource, URI baseUri)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(standardDefinitionResource,
                "Input parameter [standardDefinitionResource] cannot be null");
        Objects.requireNonNull(standardDefinitionResource.getDefinition(),
                "Input parameter [standardDefinitionResource] must have a definition");

        if (standardDefinitionResource.getDefinition().getRef() != null) {
            resolveDefinitionFromRef(standardDefinitionResource, baseUri);
        } else if (standardDefinitionResource.getDefinition().getRawContent() != null) {
            resolveDefinitionFromContent(standardDefinitionResource);
        } else {
            throw new UnresolvableReferenceException(
                    "Definition is missing. No ref and no content.");
        }
    }

    private void resolveDefinitionFromRef(StandardDefinitionDPDS stdDefResource, URI baseURI)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(stdDefResource.getDefinition());
        Objects.requireNonNull(stdDefResource.getDefinition().getRef());

        ObjectNode stdDefNode = null, defNode = null;
        try {
            stdDefNode = (ObjectNode) context.getMapper().readTree(stdDefResource.getRawContent());
            defNode = (ObjectNode) stdDefNode.get("definition");
            if (defNode == null) {
                logger.warn("No definition raw content for stdDef [" + stdDefResource.getName() + "]");
                return;
            }
        } catch (JsonProcessingException e) {
            throw new DeserializationException(
                    "Impossible to parse raw content of stdDef [" + stdDefResource.getName() + "]", e);
        }

        String defRef = null, defContent = null;
        String ref = stdDefResource.getDefinition().getRef();

        if (ref != null && ref.startsWith(context.getOptions().getServerUrl())) {
            logger.debug("Definition for stdDef [" + stdDefResource.getName() + "] has been already processed");
            return;
        }

        URI uri = null;
        try {
            uri = new URI(ref).normalize();
            if (baseURI == null) {
                baseURI = context.getLocation().getRootDocumentBaseUri();
            }
            defContent = context.getLocation().fetchResource(baseURI, uri);
        } catch (Exception e) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference [" + ref + "]",
                    e);
        }
       
        defRef = context.getOptions().getServerUrl() + "/apis/{apiId}";
        defRef = context.getOptions().getServerUrl() + "/templates/{templateId}";
        
        defNode.put("$ref", defRef);
        stdDefResource.getDefinition().setOriginalRef(ref);

    
        stdDefResource.getDefinition().setRef(defRef);
        stdDefResource.getDefinition().setRawContent(defContent);

        try {
            String apiContent = context.getMapper().writeValueAsString(stdDefNode);
            stdDefResource.setRawContent(apiContent);
        } catch (JsonProcessingException e) {
            throw new DeserializationException("Impossible serialize descriptor", e);
        }
    }

    // TODO move into deserializer
    private void resolveDefinitionFromContent(StandardDefinitionDPDS apiResource)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(apiResource.getDefinition());
        Objects.requireNonNull(apiResource.getDefinition().getRawContent());

        ObjectNode apiNode = null, apiDefinitionNode = null;
        try {
            apiNode = (ObjectNode) context.getMapper().readTree(apiResource.getRawContent());
            apiDefinitionNode = (ObjectNode) apiNode.get("definition");
        } catch (JsonProcessingException e) {
            throw new DeserializationException(
                    "Impossible to parse raw content of API [" + apiResource.getName() + "]", e);
        }

        String apiDefinitionRef = null;

        apiDefinitionRef = context.getOptions().getServerUrl() + "/apis/{apiId}";
        //apiDefinitionRef = context.getOptions().getServerUrl() + "/templates/{templateId}";
        

        apiNode.remove("definition");
        apiDefinitionNode = apiNode.putObject("definition");
        apiDefinitionNode.put("$ref", apiDefinitionRef);

        apiResource.getDefinition().setRef(apiDefinitionRef);
        apiResource.getDefinition().setMediaType("application/json");

        try {
            String apiContent = context.getMapper().writeValueAsString(apiNode);
            apiResource.setRawContent(apiContent);
        } catch (JsonProcessingException e) {
            throw new DeserializationException("Impossible serialize descriptor", e);
        }
    }

    public static void process(ParseContext context) throws UnresolvableReferenceException, DeserializationException {
        ReferencesProcessor resolver = new ReferencesProcessor(context);
        resolver.process();
    }
}
