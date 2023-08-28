package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;

import java.net.URI;
import java.util.List;

public class ExternalReferencesProcessor implements PropertiesProcessor{

    ParseContext context;
    ObjectMapper mapper;

    public ExternalReferencesProcessor(ParseContext context) {
        this.context = context;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;
    }

    @Override
    public void process() throws UnresolvableReferenceException, DeserializationException {
        DataProductVersionDPDS parsedContent =  context.getResult().getDescriptorDocument();

        if (parsedContent.getInterfaceComponents() != null) {
            resolveExternalReferences(parsedContent.getInterfaceComponents().getInputPorts(), 
                    EntityTypeDPDS.INPUTPORT);
            resolveExternalReferences(parsedContent.getInterfaceComponents().getOutputPorts(),
                    EntityTypeDPDS.OUTPUTPORT);
            resolveExternalReferences(parsedContent.getInterfaceComponents().getDiscoveryPorts(),
                    EntityTypeDPDS.DISCOVERYPORT);
            resolveExternalReferences(parsedContent.getInterfaceComponents().getObservabilityPorts(),
                    EntityTypeDPDS.OBSERVABILITYPORT);
            resolveExternalReferences(parsedContent.getInterfaceComponents().getControlPorts(),
                    EntityTypeDPDS.CONTROLPORT);
        }

        if (parsedContent.getInternalComponents() != null) {
            resolveExternalReferences(parsedContent.getInternalComponents().getApplicationComponents(),
                    EntityTypeDPDS.APPLICATION);
            resolveExternalReferences(parsedContent.getInternalComponents().getInfrastructuralComponents(),
                    EntityTypeDPDS.INFRASTRUCTURE);
        }
    }

    private <E extends ComponentDPDS> void resolveExternalReferences(List<E> components,
            EntityTypeDPDS compoEntityType) throws UnresolvableReferenceException {
        
        for (int i = 0; i < components.size(); i++) {
            E component = components.get(i);
            String ref = component.getRef();
            if (ref != null && !ref.trim().startsWith("#")) {
                components.set(i, resolveExternalReference(component));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends ComponentDPDS> E resolveExternalReference(E component)
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
            String content = context.getLocation().fetchResource(context.getLocation().getRootDocumentBaseUri(), uri);
            resolvedComponent = (E) mapper.readValue(content, component.getClass());
            resolvedComponent.setRawContent(content);
            resolvedComponent.setOriginalRef(context.getLocation().getRootDocumentBaseUri().resolve(uri).toString());
        } catch (Exception e) {
            throw new UnresolvableReferenceException(
                    "Impossible to resolve external reference [" + ref + "]",
                    e);
        }
        return resolvedComponent;
    }

    public static void process(ParseContext context) throws UnresolvableReferenceException, DeserializationException {
        ExternalReferencesProcessor resolver = new ExternalReferencesProcessor(context);
        resolver.process();
    }
}
