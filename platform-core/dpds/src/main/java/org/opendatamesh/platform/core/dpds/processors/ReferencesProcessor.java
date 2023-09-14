package org.opendatamesh.platform.core.dpds.processors;

import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;

import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.core.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.core.ComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.InternalComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleTaskInfoDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSDeserializer;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.opendatamesh.platform.core.dpds.parser.location.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
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
            resolveLifecycleInfoComponents(internalComponents.getLifecycleInfo());
        }

    }

    private void resolveLifecycleInfoComponents(LifecycleInfoDPDS lifecycleResource)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(lifecycleResource, "Input parameter [lifecycleResource] cannot be null");

        List<LifecycleTaskInfoDPDS> acivityResources = lifecycleResource.getTasksInfo();

        for (LifecycleTaskInfoDPDS acivityResource : acivityResources) {
            if (acivityResource.hasTemplate() == false)
                continue;

            StandardDefinitionDPDS templateResource = acivityResource.getTemplate();

            templateResource = resolveComponent(templateResource);
            acivityResource.setTemplate(templateResource);
        }
    }

    private <E extends ComponentDPDS> void resolveComponents(List<E> components)
            throws UnresolvableReferenceException, DeserializationException {

        for (int i = 0; i < components.size(); i++) {
            E component = null;

            component = components.get(i);
            component = resolveComponent(component);
            components.set(i, component);
        }
    }

    private <E extends ComponentDPDS> E resolveComponent(E component)
            throws UnresolvableReferenceException, DeserializationException {

        String componentRef = null;
        if (component.isExternalReference()) {
            componentRef = component.getRef();
            component = resolveComponentFromExternalRef(component);
        } else if (component.isInternalReference()) {
            component = resolveComponentFromInternalRef(component);
        }

        URI componentAbsoulutePathUri = null;
        try {
            if (componentRef != null) {
                componentAbsoulutePathUri = UriUtils.getResourceAbsolutePathUri(component.getBaseUri(), new URI(componentRef));
            } 
        } catch (Throwable t) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve absolute path uri of component [" + component.getName() + "]", t);
        }

    
        if (component instanceof PortDPDS) {
            PortDPDS port = (PortDPDS) component;
            if (port.hasApi()) {
                port.getPromises().getApi().setBaseUri(componentAbsoulutePathUri);
                StandardDefinitionDPDS api = resolveComponent(port.getPromises().getApi());
                port.getPromises().setApi(api);
            }
        }

        if (component instanceof StandardDefinitionDPDS) {
            resolveDefinition((StandardDefinitionDPDS) component, componentAbsoulutePathUri);
        }

        return component;
    }

    @SuppressWarnings("unchecked")
    private <E extends ComponentDPDS> E resolveComponentFromExternalRef(E component)
            throws UnresolvableReferenceException {

        E resolvedComponent = null;

        Objects.requireNonNull(component, "Input parameter [component] cannot be null");

        if (component.isExternalReference() == false)
            return component; // nothings to do here

        if (component.getBaseUri() == null)
            component.setBaseUri(context.getLocation().getRootDocumentBaseUri());

        try {
            URI uri = UriUtils.getResourceAbsoluteUri(component.getBaseUri(), component.getRefUri());
            String contentContent = context.getLocation().fetchResource(uri);

            DPDSDeserializer deserializer = new DPDSDeserializer();
            resolvedComponent = (E) deserializer.deserializeComponent(contentContent, component.getClass());
            resolvedComponent.setBaseUri(component.getBaseUri());
            resolvedComponent.setOriginalRef(component.getRef());
            resolvedComponent.setRawContent(contentContent);

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
            resolvedComponent.setBaseUri(component.getBaseUri());
            resolvedComponent.setOriginalRef(component.getRef());
        } else { // nothinh to do
            resolvedComponent = component;
        }

        resolvedComponent.setBaseUri(context.getLocation().getRootDocumentBaseUri());

        return resolvedComponent;
    }

    private void resolveDefinition(StandardDefinitionDPDS stdDefResource, URI stdDefAbsoulutePathUri)
            throws UnresolvableReferenceException, DeserializationException {

        Objects.requireNonNull(stdDefResource,
                "Input parameter [standardDefinitionResource] cannot be null");

        DefinitionReferenceDPDS defResource = stdDefResource.getDefinition();

        if (defResource == null || defResource.isRef() == false)
            return; // nothings to do here

        String defContent = null;
        String ref = defResource.getRef();

        if (ref != null && context.getOptions().getServerUrl() != null && ref.startsWith(context.getOptions().getServerUrl())) {
            logger.debug("Definition for stdDef [" + stdDefResource.getName() + "] has been already processed");
            return;
        }

        try {
            if(stdDefAbsoulutePathUri == null) {
                stdDefAbsoulutePathUri = context.getLocation().getRootDocumentBaseUri();
            }
            defContent = context.getLocation().fetchResource(stdDefAbsoulutePathUri, new URI(defResource.getRef()));
        } catch (Throwable t) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference [" + ref + "]",
                    t);
        }

        defResource.setRawContent(defContent);
    }

    public static void process(ParseContext context) throws UnresolvableReferenceException, DeserializationException {
        ReferencesProcessor resolver = new ReferencesProcessor(context);
        resolver.process();
    }
}
