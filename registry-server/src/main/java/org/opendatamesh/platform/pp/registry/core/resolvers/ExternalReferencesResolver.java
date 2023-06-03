package org.opendatamesh.platform.pp.registry.core.resolvers;

import java.net.URI;
import java.util.List;

import org.opendatamesh.platform.pp.registry.core.DataProductDescriptor;
import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.core.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.EntityType;

public class ExternalReferencesResolver implements PropertiesResolver{

    DataProductDescriptor descriptor;

    public ExternalReferencesResolver(DataProductDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void resolve() throws UnresolvableReferenceException, ParseException {
        DataProductVersionResource parsedContent =  descriptor.getParsedContent();

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
            String content = descriptor.getSource().fetchResource(uri);
            resolvedComponent = (E) descriptor.getObjectMapper().readValue(content, component.getClass());
            resolvedComponent.setRawContent(content);
        } catch (Exception e) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference [" + ref + "]",
                    e);
        }
        return resolvedComponent;
    }

    public static void resolve(DataProductDescriptor descriptor) throws UnresolvableReferenceException, ParseException {
        ExternalReferencesResolver resolver = new ExternalReferencesResolver(descriptor);
        resolver.resolve();
    }
}
