package org.opendatamesh.platform.core.dpds.processors;

import java.net.URI;
import java.util.List;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.ComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseContext;
import org.opendatamesh.platform.core.dpds.parser.ParseLocation;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.location.UriUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemplatesResolver {

    ParseContext context;
    private ObjectMapper mapper;

    public TemplatesResolver(ParseContext context) {
        this.context = context;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;
    }

    // Note: to be called after component resolution
    public void resolve() throws UnresolvableReferenceException, ParseException {

        if (context.getResult().getDescriptorDocument().getInterfaceComponents() == null) {
            return;
        }

        resolveTemplateObjectsInApp();
        resolveTemplateObjectsInInfra();
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
            ReferenceObjectDPDS template = null;

            serviceInfoNode = (ObjectNode) applicationNode.get("buildInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = applicationComponent.getBuildInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");
               
                templateNode = resolveReference(applicationComponent, template, templateNode, "/templates/{apiId}");
                serviceInfoNode.set("template", templateNode);
            }

            serviceInfoNode = (ObjectNode) applicationNode.get("deployInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = applicationComponent.getDeployInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");
               
                templateNode = resolveReference(applicationComponent, template, templateNode, "/templates/{apiId}");
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

    private void resolveTemplateObjectsInInfra() throws UnresolvableReferenceException, ParseException {

        List<InfrastructuralComponentDPDS> infrastructuralComponents = context.getResult().getDescriptorDocument().getInternalComponents()
                .getInfrastructuralComponents();
        for (InfrastructuralComponentDPDS infrastructuralComponent : infrastructuralComponents) {

            ObjectNode infraNode = null;
            try {
                infraNode = (ObjectNode) mapper.readTree(infrastructuralComponent.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse descriptor raw cantent", e);
            }

            ObjectNode serviceInfoNode = null;
            ObjectNode templateNode = null;
            ReferenceObjectDPDS template = null;

            serviceInfoNode = (ObjectNode) infraNode.get("provisionInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = infrastructuralComponent.getProvisionInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");
               
                templateNode = resolveReference(infrastructuralComponent, template, templateNode, "/templates/{apiId}");
                serviceInfoNode.set("template", templateNode);
            }

            try {
                String rawContent = mapper.writeValueAsString(infraNode);
                infrastructuralComponent.setRawContent(rawContent);
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible serialize descriptor", e);
            }
        }
    }

    private ObjectNode resolveReference(
            ComponentDPDS component,
            ReferenceObjectDPDS reference,
            ObjectNode referenceNode,
            String endpoint)
            throws ParseException, UnresolvableReferenceException {

        if (referenceNode == null)
            return null;

        String ref = null;
        String referenceRef = null, templateContent = null;
        if (referenceNode.get("$ref") != null) {
            ref = referenceNode.get("$ref").asText();
            try {
                URI uri = new URI(ref).normalize();
                URI baseUri = UriUtils.getBaseUri(new URI(component.getOriginalRef()));
                templateContent = context.getLocation().fetchResource(baseUri, uri);
            } catch (Exception e) {
                try {
                    referenceNode.put("comment", "Unresolvable reference");
                    templateContent = mapper.writeValueAsString(referenceNode);
                } catch (JsonProcessingException e1) {
                    throw new ParseException("Impossible serialize api definition", e1);
                }
                /* 
                throw new UnresolvableReferenceException(
                        "Impossible to resolve external reference [" + ref + "]",
                        e);
                */
            }

            referenceRef = context.getOptions().getServerUrl() + endpoint;
            referenceNode.put("$ref", referenceRef);
        } else { // inline
            try {
                templateContent = mapper.writeValueAsString(referenceNode);
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible serialize api definition", e);
            }
            referenceRef = context.getOptions().getServerUrl() + endpoint;
            referenceNode = mapper.createObjectNode();
            referenceNode.put("$ref", referenceRef);
        }

        reference.setRef(referenceRef);
        reference.setOriginalRef(ref);
        reference.setRawContent(templateContent);

        return referenceNode;

    }
   
    public static void resolve(ParseContext context) throws UnresolvableReferenceException, ParseException {
        TemplatesResolver resolver = new TemplatesResolver(context);
        resolver.resolve();
    }
}
