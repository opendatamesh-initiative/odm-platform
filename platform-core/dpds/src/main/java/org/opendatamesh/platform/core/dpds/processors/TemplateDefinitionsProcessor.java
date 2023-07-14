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
import java.util.List;


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

        resolveTemplateObjectsInApp();
        processTemplateDefinitionsInInfra();
    }

    private void resolveTemplateObjectsInApp() throws UnresolvableReferenceException, ParseException {

        List<ApplicationComponentDPDS> applicationComponents = context.getResult().getDescriptorDocument().getInternalComponents()
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

            serviceInfoNode = (ObjectNode) applicationNode.get("buildInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = applicationComponent.getBuildInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");

                templateNode = resolveTemplateDefinition(applicationComponent, template.getDefinition(), templateNode);
                serviceInfoNode.set("template", templateNode);
            }

            serviceInfoNode = (ObjectNode) applicationNode.get("deployInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = applicationComponent.getDeployInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");

                templateNode = resolveTemplateDefinition(applicationComponent, template.getDefinition(), templateNode);
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

        List<InfrastructuralComponentDPDS> infraComponents = context.getResult().getDescriptorDocument().getInternalComponents()
                .getInfrastructuralComponents();

        if (infraComponents == null || infraComponents.isEmpty()) {
            return;
        }
        
        for (InfrastructuralComponentDPDS infraComponent : infraComponents) {

            ObjectNode infraNode = null;
            try {
                infraNode = (ObjectNode) mapper.readTree(infraComponent.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse raw content of infrastructural component [" + infraComponent.getFullyQualifiedName() + "]", e);
            }

            ObjectNode serviceInfoNode = null;
            ObjectNode templateNode = null;
            StandardDefinitionDPDS template = null;

            serviceInfoNode = (ObjectNode) infraNode.get("provisionInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = infraComponent.getProvisionInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");

                templateNode = resolveTemplateDefinition(infraComponent, template.getDefinition(), templateNode);
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
            ComponentDPDS component,
            ReferenceObjectDPDS templateDefinition,
            ObjectNode templateDefinitionNode)
            throws ParseException, UnresolvableReferenceException {

        if (templateDefinitionNode == null)
            return null;

        String ref = null;
        String referenceRef = null, templateDefinitionContent = null;
        if (templateDefinitionNode.get("$ref") != null) {
            ref = templateDefinitionNode.get("$ref").asText();
            
            URI uri = null, baseUri = null;
            try {
                uri = new URI(ref).normalize();
                baseUri = UriUtils.getBaseUri(new URI(component.getOriginalRef()));
                templateDefinitionContent = context.getLocation().fetchResource(baseUri, uri);
            } catch (Exception e) {
                try {
                    templateDefinitionNode.put("comment", "Unresolvable reference");
                    templateDefinitionContent = mapper.writeValueAsString(templateDefinitionNode);
                } catch (JsonProcessingException e1) {
                    throw new ParseException("Impossible serialize template definition", e1);
                }
                /* 
                throw new UnresolvableReferenceException(
                        "Impossible to resolve external reference [" + ref + "]",
                        e);
                */
            }

            referenceRef = context.getOptions().getServerUrl() + "/templates/{templateId}";
            templateDefinitionNode.put("$ref", referenceRef);
        } else { // inline
            try {
                templateDefinitionContent = mapper.writeValueAsString(templateDefinitionNode);
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible serialize template definition", e);
            }
            referenceRef = context.getOptions().getServerUrl() + "/templates/{templateId}";
            templateDefinitionNode = mapper.createObjectNode();
            templateDefinitionNode.put("$ref", referenceRef);
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
