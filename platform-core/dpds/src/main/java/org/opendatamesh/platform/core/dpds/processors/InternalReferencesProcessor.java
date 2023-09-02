package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.ComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.InternalComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InternalReferencesProcessor implements PropertiesProcessor {

    ParseContext context;

    public InternalReferencesProcessor(ParseContext context) {
        this.context = context;
    }

    @Override
    public void process() throws DeserializationException, UnresolvableReferenceException {

        DataProductVersionDPDS parsedContent = context.getResult().getDescriptorDocument();

        ComponentsDPDS componentsObject = parsedContent.getComponents();
        if (componentsObject == null)
            return;

        InterfaceComponentsDPDS interfaceComponents = parsedContent.getInterfaceComponents();
        if (interfaceComponents != null) {
            resolveInternalReferences(interfaceComponents.getInputPorts(),
                    componentsObject.getComponentsByEntityType(EntityTypeDPDS.INPUTPORT));

            resolveInternalReferences(interfaceComponents.getOutputPorts(),
                    componentsObject.getComponentsByEntityType(EntityTypeDPDS.OUTPUTPORT));

            resolveInternalReferences(interfaceComponents.getObservabilityPorts(),
                    componentsObject.getComponentsByEntityType(EntityTypeDPDS.OBSERVABILITYPORT));
            resolveInternalReferences(interfaceComponents.getDiscoveryPorts(),
                    componentsObject.getComponentsByEntityType(EntityTypeDPDS.DISCOVERYPORT));
            resolveInternalReferences(interfaceComponents.getControlPorts(),
                    componentsObject.getComponentsByEntityType(EntityTypeDPDS.CONTROLPORT));
        }

        InternalComponentsDPDS internalComponents = parsedContent.getInternalComponents();
        if (internalComponents != null) {
            resolveInternalReferences(internalComponents.getApplicationComponents(),
                    componentsObject.getComponentsByEntityType(EntityTypeDPDS.APPLICATION));
            resolveInternalReferences(internalComponents.getInfrastructuralComponents(),
                    componentsObject.getComponentsByEntityType(EntityTypeDPDS.INFRASTRUCTURE));

            if (internalComponents.hasLifecycleInfo()) {
                List<LifecycleActivityInfoDPDS> acivityResources = internalComponents.getLifecycleInfo()
                        .getActivityInfos();

                for (LifecycleActivityInfoDPDS acivityResource : acivityResources) {
                    if (acivityResource.hasTemplate()) {
                        StandardDefinitionDPDS templateResource = acivityResource.getTemplate();
                        StandardDefinitionDPDS resolvedComponent = resolveInternalReference(templateResource,
                                componentsObject.getComponentsByEntityType(EntityTypeDPDS.TEMPLATE));
                        acivityResource.setTemplate(resolvedComponent);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends ComponentDPDS> void resolveInternalReferences(
            List<E> components,
            Map<String, ComponentDPDS> sharedComponents) throws UnresolvableReferenceException {

        for (int i = 0; i < components.size(); i++) {

            E component = components.get(i);
            // internal ref example : #/components/infrastructuralComponents/eventStore
            ComponentDPDS resolvedComponent = resolveInternalReference(component, sharedComponents);
            components.set(i, (E) resolvedComponent);
        }
    }

    private <E extends ComponentDPDS> E resolveInternalReference(
            E component,
            Map<String, ComponentDPDS> sharedComponents) throws UnresolvableReferenceException {

        E resolvedComponent = null;

        if (component.isInternalReference()) {
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

    public static void process(ParseContext context) throws DeserializationException, UnresolvableReferenceException {
        InternalReferencesProcessor resolver = new InternalReferencesProcessor(context);
        resolver.process();
    }
}
