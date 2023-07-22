package org.opendatamesh.platform.core.dpds.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.*;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.opendatamesh.platform.core.dpds.parser.location.UriUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

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
        resolveTemplateObjectsInApp();
        processTemplateDefinitionsInInfra();
    }

    private void resolveTemplateObjectsInLifecycle() throws UnresolvableReferenceException, ParseException {

        InternalComponentsDPDS internalComponents = context.getResult().getDescriptorDocument().getInternalComponents();
        if (internalComponents == null)
            return; // Nothings to do

        LifecycleInfoDPDS lifecycleInfo = internalComponents.getLifecycleInfo();
        if (lifecycleInfo == null)
            return; // Nothings to do

        ObjectNode lifecycleNode = null;
        try {
            lifecycleNode = (ObjectNode) mapper.readTree(lifecycleInfo.getRawContent());
        } catch (JsonProcessingException e) {
            throw new ParseException("Impossible to parse descriptor raw cantent", e);
        }

        Set<Entry<String, ActivityInfoDPDS>> stages = lifecycleInfo.getStages().entrySet();

        for (Entry<String, ActivityInfoDPDS> stage : stages) {

            String stageName = stage.getKey();
            ActivityInfoDPDS stageActivity = stage.getValue();

            ObjectNode activityNode = (ObjectNode) lifecycleNode.get(stageName);
            ObjectNode templateNode = (ObjectNode) activityNode.get("template");
            if(templateNode == null) continue; // Nothings to do
            ObjectNode templateDefinitionNode = (ObjectNode) templateNode.get("definition");
            StandardDefinitionDPDS template = stageActivity.getTemplate();

            if (template != null) {
                URI baseUri = context.getLocation().getRootDocumentBaseUri();
                templateDefinitionNode = resolveTemplateDefinition(baseUri, template.getDefinition(), templateDefinitionNode);
                templateNode.set("definition", templateDefinitionNode);
            }
        }

        try {
            String rawContent = mapper.writeValueAsString(lifecycleNode);
            lifecycleInfo.setRawContent(rawContent);
        } catch (JsonProcessingException e) {
            throw new ParseException("Impossible serialize descriptor", e);
        }
    }

    private void resolveTemplateObjectsInApp() throws UnresolvableReferenceException, ParseException {

        if (context.getResult().getDescriptorDocument().getInternalComponents() == null)
            return;

        List<ApplicationComponentDPDS> applicationComponents = context.getResult().getDescriptorDocument()
                .getInternalComponents()
                .getApplicationComponents();
        for (ApplicationComponentDPDS applicationComponent : applicationComponents) {

            ObjectNode applicationNode = null;
            try {
                applicationNode = (ObjectNode) mapper.readTree(applicationComponent.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse descriptor raw cantent", e);
            }

            ObjectNode serviceInfoNode = null;
            ObjectNode templateNode = null;
            StandardDefinitionDPDS template = null;

            URI baseUri = null;
            if(applicationComponent.getOriginalRef() != null) {
                try {
                    baseUri = UriUtils.getBaseUri(new URI(applicationComponent.getOriginalRef()));
                } catch (URISyntaxException e) {
                    new UnresolvableReferenceException(
                            "Impossible to resolve baseUri [" + applicationComponent.getOriginalRef() + "]", e);
                }
            }
            

            serviceInfoNode = (ObjectNode) applicationNode.get("buildInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = applicationComponent.getBuildInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");

                templateNode = resolveTemplateDefinition(baseUri, template.getDefinition(), templateNode);
                serviceInfoNode.set("template", templateNode);
            }

            serviceInfoNode = (ObjectNode) applicationNode.get("deployInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = applicationComponent.getDeployInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");

                templateNode = resolveTemplateDefinition(baseUri, template.getDefinition(), templateNode);
                serviceInfoNode.set("template", templateNode);
            }

            try {
                String rawContent = mapper.writeValueAsString(applicationNode);
                applicationComponent.setRawContent(rawContent);
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible serialize descriptor", e);
            }
        }
    }

    private void processTemplateDefinitionsInInfra() throws UnresolvableReferenceException, ParseException {

        if (context.getResult().getDescriptorDocument().getInternalComponents() == null)
            return;

        List<InfrastructuralComponentDPDS> infraComponents = context.getResult().getDescriptorDocument()
                .getInternalComponents()
                .getInfrastructuralComponents();

        if (infraComponents == null || infraComponents.isEmpty()) {
            return;
        }

        for (InfrastructuralComponentDPDS infraComponent : infraComponents) {

            ObjectNode infraNode = null;
            try {
                infraNode = (ObjectNode) mapper.readTree(infraComponent.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse raw content of infrastructural component ["
                        + infraComponent.getFullyQualifiedName() + "]", e);
            }

            ObjectNode serviceInfoNode = null;
            ObjectNode templateNode = null;
            StandardDefinitionDPDS template = null;

            URI baseUri = null;
            if(infraComponent.getOriginalRef() != null) {
                try {
                    baseUri = UriUtils.getBaseUri(new URI(infraComponent.getOriginalRef()));
                } catch (URISyntaxException e) {
                    new UnresolvableReferenceException(
                            "Impossible to resolve baseUri [" + infraComponent.getOriginalRef() + "]", e);
                }
            }
            

            serviceInfoNode = (ObjectNode) infraNode.get("provisionInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = infraComponent.getProvisionInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");

                templateNode = resolveTemplateDefinition(baseUri, template.getDefinition(), templateNode);
                serviceInfoNode.set("template", templateNode);
            }

            try {
                String rawContent = mapper.writeValueAsString(infraNode);
                infraComponent.setRawContent(rawContent);
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
                // baseUri = UriUtils.getBaseUri(new URI(component.getOriginalRef()));
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
            if(templateDefinitionNode.get("$originalRef") != null) {
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
