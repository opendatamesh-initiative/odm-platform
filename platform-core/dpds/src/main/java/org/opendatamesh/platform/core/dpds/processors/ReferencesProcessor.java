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
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSDeserializer;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReferencesProcessor implements PropertiesProcessor {

    ParseContext context;

    public ReferencesProcessor(ParseContext context) {
        this.context = context;
    }

    @Override
    public void process() throws UnresolvableReferenceException, DeserializationException {
        DataProductVersionDPDS descriptorResource = context.getResult().getDescriptorDocument();

        URI baseUri = context.getLocation().getRootDocumentBaseUri();

        InterfaceComponentsDPDS interfaceComponents = descriptorResource.getInterfaceComponents();
        if (interfaceComponents != null) {
            resolveReferences(interfaceComponents.getInputPorts(), baseUri);
            resolveReferences(interfaceComponents.getOutputPorts(), baseUri);
            resolveReferences(interfaceComponents.getDiscoveryPorts(), baseUri);
            resolveReferences(interfaceComponents.getObservabilityPorts(), baseUri);
            resolveReferences(interfaceComponents.getControlPorts(), baseUri);
        }

        InternalComponentsDPDS internalComponents = descriptorResource.getInternalComponents();
        if (internalComponents != null) {
            resolveReferences(internalComponents.getApplicationComponents(), baseUri);
            resolveReferences(internalComponents.getInfrastructuralComponents(), baseUri);

            if (internalComponents.hasLifecycleInfo()) {
                List<LifecycleActivityInfoDPDS> acivityResources = internalComponents.getLifecycleInfo()
                        .getActivityInfos();

                for (LifecycleActivityInfoDPDS acivityResource : acivityResources) {
                    if (acivityResource.hasTemplate() && acivityResource.getTemplate().isReference()) {
                        StandardDefinitionDPDS templateResource = acivityResource.getTemplate();
                        StandardDefinitionDPDS resolvedComponent = resolveReference(templateResource, baseUri);
                        acivityResource.setTemplate(resolvedComponent);
                    }
                }
            }
        }

    }

    private <E extends ComponentDPDS> void resolveReferences(List<E> components, URI baseURI)
            throws UnresolvableReferenceException {

        for (int i = 0; i < components.size(); i++) {
            E component = components.get(i);
            if (component.isReference()) {
                components.set(i, resolveReference(component, baseURI));
            }
        }
    }

    private <E extends ComponentDPDS> E resolveReference(E component, URI baseURI)
            throws UnresolvableReferenceException {

        E resolvedComponent = null;

        if (component.isExternalReference()) {
            resolvedComponent = resolveExternalReference(component, baseURI);
        } else if (component.isInternalReference()) {
            resolvedComponent = resolveInternalReference(component);
        } else {
             throw new UnresolvableReferenceException(
                "Impossible to resolve reference [" + component.getRef() + "]");
        }

        return resolvedComponent;
    }

    @SuppressWarnings("unchecked")
    private <E extends ComponentDPDS> E resolveExternalReference(E component, URI baseURI)
            throws UnresolvableReferenceException {

        E resolvedComponent = null;

        try {
            URI uri = new URI(component.getRef()).normalize();
            String content = context.getLocation().fetchResource(baseURI, uri);

            DPDSDeserializer deserializer = new DPDSDeserializer();
            resolvedComponent = (E) deserializer.deserializeComponent(content, component.getClass());
            resolvedComponent.setRawContent(content);
            resolvedComponent.setOriginalRef(baseURI.resolve(uri).toString());
        } catch (Throwable t) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference [" + component.getRef() + "]",
                    t);
        }

        return resolvedComponent;
    }

    private <E extends ComponentDPDS> E resolveInternalReference(E component) throws UnresolvableReferenceException {

        E resolvedComponent = null;

        if (component.isInternalReference()) {
            DataProductVersionDPDS descriptorResource = context.getResult().getDescriptorDocument();
            ComponentsDPDS componentsResource = descriptorResource.getComponents();
            String x = component.getInternalReferenceGroupName();
            EntityTypeDPDS type = EntityTypeDPDS.resolveGroupingPropertyName(x);
            if(type == null) {
                System.out.println(x + " : " + component.getRef());
            }
            Map<String, ComponentDPDS>  sharedComponents = componentsResource.getComponentsByEntityType(type);
        
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

    public static void process(ParseContext context) throws UnresolvableReferenceException, DeserializationException {
        ReferencesProcessor resolver = new ReferencesProcessor(context);
        resolver.process();
    }
}
