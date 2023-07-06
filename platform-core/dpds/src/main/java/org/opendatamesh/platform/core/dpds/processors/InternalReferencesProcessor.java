package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.ComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;

import java.util.List;

public class InternalReferencesProcessor implements PropertiesProcessor{

    ParseContext context;
    ObjectMapper mapper;

    public InternalReferencesProcessor(ParseContext context) {
        this.context = context;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;
    }

    @Override
    public void process() throws ParseException, UnresolvableReferenceException {

        DataProductVersionDPDS parsedContent = context.getResult().getDescriptorDocument();

        if (parsedContent.getInterfaceComponents() != null) {
            resolveInternalReferences(parsedContent.getInterfaceComponents().getOutputPorts(),
                    parsedContent.getComponents(), EntityTypeDPDS.outputport);
            resolveInternalReferences(parsedContent.getInterfaceComponents().getInputPorts(),
                    parsedContent.getComponents(), EntityTypeDPDS.inputport);
            resolveInternalReferences(parsedContent.getInterfaceComponents().getObservabilityPorts(),
                    parsedContent.getComponents(), EntityTypeDPDS.observabilityport);
            resolveInternalReferences(parsedContent.getInterfaceComponents().getDiscoveryPorts(),
                    parsedContent.getComponents(), EntityTypeDPDS.discoveryport);
            resolveInternalReferences(parsedContent.getInterfaceComponents().getControlPorts(),
                    parsedContent.getComponents(), EntityTypeDPDS.controlport);
        }
        if (parsedContent.getInternalComponents() != null) {
            resolveInternalReferences(parsedContent.getInternalComponents().getApplicationComponents(),
                    parsedContent.getComponents(), EntityTypeDPDS.application);
            resolveInternalReferences(parsedContent.getInternalComponents().getInfrastructuralComponents(),
                    parsedContent.getComponents(), EntityTypeDPDS.infrastructure);
        }

        //descriptor.setParsedContent(parsedContent);
        //descriptor.setRawContent(mapper.getParsedContentAsString(descriptor.getParsedContent(), false));
    }

    private <E extends ComponentDPDS> void resolveInternalReferences(List<E> components,
            ComponentsDPDS componentsObject,
            EntityTypeDPDS type) throws UnresolvableReferenceException {
        for (int i = 0; i < components.size(); i++) {

            E component = components.get(i);
            String ref = component.getRef();
            if (ref != null && ref.trim().startsWith("#")) {

                // internal ref example : #/components/infrastructuralComponents/eventStore
                ComponentDPDS resovedComponent = componentsObject.getComponentsByEntityType(type)
                        .get(ref.substring(ref.lastIndexOf("/")));

                if (resovedComponent == null) {
                    throw new UnresolvableReferenceException(
                            "Impossible to resolve internal reference [" + ref + "]");
                }

                components.set(i, (E) resovedComponent);
            }
        }
    }

    public static void process(ParseContext context)  throws ParseException, UnresolvableReferenceException {
        InternalReferencesProcessor resolver = new InternalReferencesProcessor(context);
        resolver.process();
    }
}
