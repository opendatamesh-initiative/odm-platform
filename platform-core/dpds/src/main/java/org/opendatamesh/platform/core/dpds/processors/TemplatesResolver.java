package org.opendatamesh.platform.core.dpds.processors;

import java.net.URI;
import java.util.List;

import org.opendatamesh.platform.core.dpds.DataProductVersionSource;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemplatesResolver {

    DataProductVersionDPDS dataProductVersion;
    DataProductVersionSource source;
    private String targetURL;
    private ObjectMapper mapper;

    public TemplatesResolver(DataProductVersionDPDS dataProductVersion, DataProductVersionSource source,
            String targetURL) {
        this.dataProductVersion = dataProductVersion;
        this.source = source;
        this.targetURL = targetURL;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;
    }

    // Note: to be called after component resolution
    public void resolve() throws UnresolvableReferenceException, ParseException {

        if (dataProductVersion.getInterfaceComponents() == null) {
            return;
        }

        resolveTemplateObjectsInApp();
        resolveTemplateObjectsInInfra();
    }

    private void resolveTemplateObjectsInApp() throws UnresolvableReferenceException, ParseException {

        List<ApplicationComponentDPDS> applicationComponents = dataProductVersion.getInternalComponents()
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
               
                templateNode = resolveReference(template, templateNode, "/templates/{apiId}");
                serviceInfoNode.set("template", templateNode);
            }

            serviceInfoNode = (ObjectNode) applicationNode.get("deployInfo");
            if (serviceInfoNode != null && serviceInfoNode.get("template") != null) {

                template = applicationComponent.getDeployInfo().getTemplate();
                templateNode = (ObjectNode) serviceInfoNode.get("template");
               
                templateNode = resolveReference(template, templateNode, "/templates/{apiId}");
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

        List<InfrastructuralComponentDPDS> infrastructuralComponents = dataProductVersion.getInternalComponents()
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
               
                templateNode = resolveReference(template, templateNode, "/templates/{apiId}");
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
                templateContent = source.fetchResource(uri);
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

            referenceRef = targetURL + endpoint;
            referenceNode.put("$ref", referenceRef);
        } else { // inline
            try {
                templateContent = mapper.writeValueAsString(referenceNode);
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible serialize api definition", e);
            }
            referenceRef = targetURL + endpoint;
            referenceNode = mapper.createObjectNode();
            referenceNode.put("$ref", referenceRef);
        }

        reference.setRef(referenceRef);
        reference.setOriginalRef(ref);
        reference.setRawContent(templateContent);

        return referenceNode;

    }

    

   
    public static void resolve(DataProductVersionDPDS dataProductVersionRes, DataProductVersionSource source,
            String targetURL) throws UnresolvableReferenceException, ParseException {
        TemplatesResolver resolver = new TemplatesResolver(dataProductVersionRes, source,
                targetURL);
        resolver.resolve();
    }
}
