package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.DeserializationException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.*;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;

import java.net.URI;

public class TemplateDefinitionsProcessor implements PropertiesProcessor {

    ParseContext context;

    public TemplateDefinitionsProcessor(ParseContext context) {
        this.context = context;
    }

    // Note: to be called after component resolution
    @Override
    public void process() throws UnresolvableReferenceException, DeserializationException {

       resolveTemplateObjectsInLifecycle();

    }

    private void resolveTemplateObjectsInLifecycle() throws UnresolvableReferenceException, DeserializationException {

        InternalComponentsDPDS internalComponents = context.getResult().getDescriptorDocument().getInternalComponents();
        if (internalComponents == null)
            return; // Nothings to do

        LifecycleInfoDPDS lifecycleInfo = internalComponents.getLifecycleInfo();
        if (lifecycleInfo == null)
            return; // Nothings to do

        for (LifecycleActivityInfoDPDS activity : lifecycleInfo.getActivityInfos()) {

            try {
                if(activity.hasTemplate() == false) continue; // Nothings to do
                ObjectNode templateNode = (ObjectNode) context.getMapper().readTree(activity.getTemplate().getRawContent());
                                    
                ObjectNode templateDefinitionNode = (ObjectNode) templateNode.get("definition");
                StandardDefinitionDPDS template = activity.getTemplate();

                if (template != null) {
                    URI baseUri = context.getLocation().getRootDocumentBaseUri();
                    templateDefinitionNode = resolveTemplateDefinition(baseUri, template.getDefinition(),
                            templateDefinitionNode);
                    templateNode.set("definition", templateDefinitionNode);

                    String rawContent = context.getMapper().writeValueAsString(templateNode);
                    activity.getTemplate().setRawContent(rawContent);
                }
            } catch (JsonProcessingException e) {
                throw new DeserializationException("Impossible serialize descriptor", e);
            }

        }

    }

    private ObjectNode resolveTemplateDefinition(
            URI baseUri,
            ReferenceObjectDPDS templateDefinition,
            ObjectNode templateDefinitionNode)
            throws DeserializationException, UnresolvableReferenceException {

        if (templateDefinitionNode == null)
            return null;

        String ref = null;
        String referenceRef = null, templateDefinitionContent = null;
        if (templateDefinitionNode.get("$ref") != null) {
            ref = templateDefinitionNode.get("$ref").asText();

            if (ref.startsWith(context.getOptions().getServerUrl()))
                return templateDefinitionNode;

            URI uri = null;
            try {
                uri = new URI(ref).normalize();
                templateDefinitionContent = context.getLocation().fetchResource(baseUri, uri);
            } catch (Exception e) {
                try {
                    templateDefinitionNode.put("comment", "Unresolvable reference");
                    templateDefinitionContent = context.getMapper().writeValueAsString(templateDefinitionNode);
                } catch (JsonProcessingException e1) {
                    throw new DeserializationException("Impossible serialize template definition", e1);
                }
            }

            referenceRef = context.getOptions().getServerUrl() + "/templates/{templateId}";
            templateDefinitionNode.put("$ref", referenceRef);

        } else { // inline
            try {
                templateDefinitionContent = context.getMapper().writeValueAsString(templateDefinitionNode);
            } catch (JsonProcessingException e) {
                throw new DeserializationException("Impossible serialize template definition", e);
            }
            if (templateDefinitionNode.get("$originalRef") != null) {
                ref = templateDefinitionNode.get("$originalRef").asText(); // in case it was an internal reference
            }
            referenceRef = context.getOptions().getServerUrl() + "/templates/{templateId}";
            templateDefinitionNode = context.getMapper().createObjectNode();
            templateDefinitionNode.put("$ref", referenceRef);
            templateDefinitionNode.put("$originalRef", ref);
            templateDefinitionNode.put("mediaType", "application/json");
            templateDefinition.setMediaType("application/json");
        }

        templateDefinition.setRef(referenceRef);
        templateDefinition.setOriginalRef(ref);
        templateDefinition.setRawContent(templateDefinitionContent);

        return templateDefinitionNode;

    }

    public static void resolve(ParseContext context) throws UnresolvableReferenceException, DeserializationException {
        TemplateDefinitionsProcessor resolver = new TemplateDefinitionsProcessor(context);
        resolver.process();
    }

}
