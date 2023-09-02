package org.opendatamesh.platform.core.dpds.processors;

import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
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

public class ReferencesProcessor implements PropertiesProcessor {

    ParseContext context;

    public ReferencesProcessor(ParseContext context) {
        this.context = context;
    }

    @Override
    public void process() throws UnresolvableReferenceException, DeserializationException {
        DataProductVersionDPDS parsedContent =  context.getResult().getDescriptorDocument();

        URI baseUri = context.getLocation().getRootDocumentBaseUri();

        InterfaceComponentsDPDS interfaceComponents = parsedContent.getInterfaceComponents();
        if (interfaceComponents != null) {
            resolveExternalReferences(interfaceComponents.getInputPorts(), baseUri);
            resolveExternalReferences(interfaceComponents.getOutputPorts(), baseUri);
            resolveExternalReferences(interfaceComponents.getDiscoveryPorts(), baseUri);
            resolveExternalReferences(interfaceComponents.getObservabilityPorts(), baseUri);
            resolveExternalReferences(interfaceComponents.getControlPorts(), baseUri);
        }

        InternalComponentsDPDS internalComponents = parsedContent.getInternalComponents();
        if (internalComponents != null) {
            resolveExternalReferences(internalComponents.getApplicationComponents(), baseUri);
            resolveExternalReferences(internalComponents.getInfrastructuralComponents(), baseUri);

            if(internalComponents.hasLifecycleInfo()) {
                List<LifecycleActivityInfoDPDS> acivityResources = internalComponents.getLifecycleInfo().getActivityInfos();
                
                List<StandardDefinitionDPDS> templateResources = new ArrayList<StandardDefinitionDPDS>();
                for(LifecycleActivityInfoDPDS acivityResource : acivityResources) {
                    if(acivityResource.hasTemplate()) {
                        templateResources.add(acivityResource.getTemplate());
                    }
                }
                resolveExternalReferences(templateResources, baseUri);
            }
        }

        
    }

    private <E extends ComponentDPDS> void resolveExternalReferences(List<E> components, URI baseURI) throws UnresolvableReferenceException {
        
        for (int i = 0; i < components.size(); i++) {
            E component = components.get(i);
            if (component.isExternalReference()) {
                components.set(i, resolveExternalReference(component, baseURI));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends ComponentDPDS> E resolveExternalReference(E component, URI baseURI)
            throws UnresolvableReferenceException {
        
        E resolvedComponent = null;
        
        if (!component.isExternalReference()) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference. Field [$ref] value [" + component.getRef()
                            + "] is not an URL to an external resource");
        }

        try {
            URI uri = new URI(component.getRef()).normalize();
            String content = context.getLocation().fetchResource(baseURI, uri);

            DPDSDeserializer deserializer = new DPDSDeserializer();
            resolvedComponent = (E)deserializer.deserializeComponent(content, component.getClass());
            resolvedComponent.setRawContent(content);
            resolvedComponent.setOriginalRef(baseURI.resolve(uri).toString());
        } catch (Throwable t) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference [" + component.getRef() + "]",
                    t);
        }
        
        return resolvedComponent;
    }

    public static void process(ParseContext context) throws UnresolvableReferenceException, DeserializationException {
        ReferencesProcessor resolver = new ReferencesProcessor(context);
        resolver.process();
    }
}
