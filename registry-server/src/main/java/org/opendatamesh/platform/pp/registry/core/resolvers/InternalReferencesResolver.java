package org.opendatamesh.platform.pp.registry.core.resolvers;

import java.util.List;

import org.opendatamesh.platform.pp.registry.core.DataProductDescriptor;
import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.core.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ComponentsResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.EntityType;

public class InternalReferencesResolver implements PropertiesResolver{

    DataProductDescriptor descriptor;

    public InternalReferencesResolver(DataProductDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void resolve() throws ParseException, UnresolvableReferenceException {

        DataProductVersionResource parsedContent = descriptor.getParsedContent();

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

        descriptor.setParsedContent(parsedContent);
        descriptor.setRawContent(descriptor.getParsedContentAsString());
        ;
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
                    throw new UnresolvableReferenceException(
                            "Impossible to resolve internal reference [" + ref + "]");
                }

                components.set(i, (E) resovedComponent);
            }
        }
    }

    public static void resolve(DataProductDescriptor descriptor) throws ParseException, UnresolvableReferenceException {
        InternalReferencesResolver resolver = new InternalReferencesResolver(descriptor);
        resolver.resolve();
    }
}
