package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.*;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;

import java.net.URI;

public class TemplateDefinitionsProcessor {

    ParseContext context;
    private ObjectMapper mapper;

    public TemplateDefinitionsProcessor(ParseContext context) {
        this.context = context;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;
    }

    // Note: to be called after component resolution
    public void resolve() throws UnresolvableReferenceException, ParseException {

        if (context.getResult().getDescriptorDocument().getInterfaceComponents() == null) {
            return;
        }

        resolveTemplateObjectsInLifecycle();

    }

    private void resolveTemplateObjectsInLifecycle() throws UnresolvableReferenceException, ParseException {

        InternalComponentsDPDS internalComponents = context.getResult().getDescriptorDocument().getInternalComponents();
        if (internalComponents == null)
            return; // Nothings to do

        LifecycleInfoDPDS lifecycleInfo = internalComponents.getLifecycleInfo();
        if (lifecycleInfo == null)
            return; // Nothings to do

        for (LifecycleActivityInfoDPDS activity : lifecycleInfo.getActivityInfos()) {

            try {
                ObjectNode activityNode = (ObjectNode) mapper.readTree(activity.getRawContent());
                ObjectNode templateNode = (ObjectNode) activityNode.get("template");
                if (templateNode == null)
                    continue; // Nothings to do
                ObjectNode templateDefinitionNode = (ObjectNode) templateNode.get("definition");
                StandardDefinitionDPDS template = activity.getTemplate();

                if (template != null) {
                    URI baseUri = context.getLocation().getRootDocumentBaseUri();
                    templateDefinitionNode = resolveTemplateDefinition(baseUri, template.getDefinition(),
                            templateDefinitionNode);
                    templateNode.set("definition", templateDefinitionNode);

                    String rawContent = mapper.writeValueAsString(activityNode);
                    activity.setRawContent(rawContent);
                }
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible serialize descriptor", e);
            }

        }

    }

    private ObjectNode resolveTemplateDefinition(
            URI baseUri,
            ReferenceObjectDPDS templateDefinition,
            ObjectNode templateDefinitionNode)
            throws ParseException, UnresolvableReferenceException {

        if (templateDefinitionNode == null)
            return null;

        String ref = null;
        String referenceRef = null, templateDefinitionContent = null;
        if (templateDefinitionNode.get("$ref") != null) {
            ref = templateDefinitionNode.get("$ref").asText();

            URI uri = null;
            try {
                uri = new URI(ref).normalize();
                templateDefinitionContent = context.getLocation().fetchResource(baseUri, uri);
            } catch (Exception e) {
                try {
                    templateDefinitionNode.put("comment", "Unresolvable reference");
                    templateDefinitionContent = mapper.writeValueAsString(templateDefinitionNode);
                } catch (JsonProcessingException e1) {
                    throw new ParseException("Impossible serialize template definition", e1);
                }
            }

            referenceRef = context.getOptions().getServerUrl() + "/templates/{templateId}";
            templateDefinitionNode.put("$ref", referenceRef);

        } else { // inline
            try {
                templateDefinitionContent = mapper.writeValueAsString(templateDefinitionNode);
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible serialize template definition", e);
            }
            if (templateDefinitionNode.get("$originalRef") != null) {
                ref = templateDefinitionNode.get("$originalRef").asText(); // in case it was an internal reference
            }
            referenceRef = context.getOptions().getServerUrl() + "/templates/{templateId}";
            templateDefinitionNode = mapper.createObjectNode();
            templateDefinitionNode.put("$ref", referenceRef);
            templateDefinitionNode.put("$originalRef", ref);
            templateDefinition.setMediaType("application/json");
        }

        templateDefinition.setRef(referenceRef);
        templateDefinition.setOriginalRef(ref);
        templateDefinition.setRawContent(templateDefinitionContent);

        return templateDefinitionNode;

    }

    public static void resolve(ParseContext context) throws UnresolvableReferenceException, ParseException {
        TemplateDefinitionsProcessor resolver = new TemplateDefinitionsProcessor(context);
        resolver.resolve();
    }
}
