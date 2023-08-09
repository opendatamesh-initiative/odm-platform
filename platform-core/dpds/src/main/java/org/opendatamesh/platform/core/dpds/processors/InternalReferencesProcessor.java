package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.ComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
            
            if(parsedContent.getInternalComponents().getLifecycleInfo() != null) {
                resolveInternalReferences(parsedContent.getInternalComponents().getLifecycleInfo(), parsedContent.getComponents());
            }
            
        }

        
    }

    @SuppressWarnings("unchecked")
    private <E extends ComponentDPDS> void resolveInternalReferences(List<E> components,
            ComponentsDPDS componentsObject,
            EntityTypeDPDS type) throws UnresolvableReferenceException {
        for (int i = 0; i < components.size(); i++) {

            E component = components.get(i);
            String ref = component.getRef();
            if (ref != null && ref.trim().startsWith("#")) {

                // internal ref example : #/components/infrastructuralComponents/eventStore
                ComponentDPDS resovedComponent = componentsObject.getComponentsByEntityType(type)
                        .get(ref.substring(ref.lastIndexOf("/")+1));

                if (resovedComponent == null) {
                    throw new UnresolvableReferenceException(
                            "Impossible to resolve internal reference [" + ref + "]");
                }

                components.set(i, (E) resovedComponent);
            }
        }
    }

    private void resolveInternalReferences(LifecycleInfoDPDS lifecycleInfo, ComponentsDPDS componentsObject) throws UnresolvableReferenceException {
        Objects.requireNonNull(lifecycleInfo, "Parameter [lifecycleInfo] cannot be null");
        
        for(LifecycleActivityInfoDPDS activity : lifecycleInfo.getActivityInfos()) {
            
            if(activity.getTemplate() == null) continue; // Nothings to do
            DefinitionReferenceDPDS templateDefinition = activity.getTemplate().getDefinition();
            String ref = templateDefinition!=null? templateDefinition.getRef(): null; 
            if (ref != null && ref.trim().startsWith("#")) {

                // internal ref example : "#components/templates/dpdLifecyclePipe"
                String templateName = ref.substring(ref.lastIndexOf("/")+1);
                ObjectNode resolvedTemplateDefinitionNode = componentsObject.getTemplates().get(templateName);

                if (resolvedTemplateDefinitionNode == null) {
                    throw new UnresolvableReferenceException(
                            "Impossible to resolve internal reference [" + ref + "]");
                }

                
                try {
                    DefinitionReferenceDPDS resolvedTemplateDefinition = new DefinitionReferenceDPDS();
                    resolvedTemplateDefinition.setMediaType("application/json");
                    resolvedTemplateDefinition.setOriginalRef(ref);
                    resolvedTemplateDefinition.setRawContent(ObjectMapperFactory.JSON_MAPPER.writeValueAsString(resolvedTemplateDefinitionNode));
                    activity.getTemplate().setDefinition(resolvedTemplateDefinition);

                    ObjectNode activityNode = (ObjectNode)ObjectMapperFactory.JSON_MAPPER.readTree(activity.getRawContent());
                    ObjectNode templateNode =  (ObjectNode)activityNode.at("/template");
                    if(!templateNode.isMissingNode()) {
                        resolvedTemplateDefinitionNode.put("mediaType", resolvedTemplateDefinition.getMediaType());
                        resolvedTemplateDefinitionNode.put("$originalRef", resolvedTemplateDefinition.getOriginalRef());
                        templateNode.set("definition", resolvedTemplateDefinitionNode);
                        activity.setRawContent(ObjectMapperFactory.JSON_MAPPER.writeValueAsString(activityNode));
                    }
                } catch (Exception e) {
                    throw new UnresolvableReferenceException("Impossible to parse lifecycle raw content while resolving internal reference [" + ref + "]", e);
                } 
            }
        }
    }

    public static void process(ParseContext context)  throws ParseException, UnresolvableReferenceException {
        InternalReferencesProcessor resolver = new InternalReferencesProcessor(context);
        resolver.process();
    }
}
