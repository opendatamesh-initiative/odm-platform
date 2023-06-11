package org.opendatamesh.platform.pp.registry.core.resolvers;

import java.net.URI;
import java.util.List;

import org.opendatamesh.platform.pp.registry.core.DataProductVersionSource;
import org.opendatamesh.platform.pp.registry.core.ObjectMapperFactory;
import org.opendatamesh.platform.pp.registry.core.DataProductVersionSerializer;
import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.core.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.InfrastructuralComponent;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ApplicationComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InfrastructuralComponentResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.PortResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.ReferenceObjectResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemplatesResolver {

    DataProductVersionResource dataProductVersionRes;
    DataProductVersionSource source;
    private String targetURL;
    private ObjectMapper mapper;

    public TemplatesResolver(DataProductVersionResource dataProductVersionRes, DataProductVersionSource source,
            String targetURL) {
        this.dataProductVersionRes = dataProductVersionRes;
        this.source = source;
        this.targetURL = targetURL;
        this.mapper = ObjectMapperFactory.JSON_MAPPER;
    }

    // Note: to be called after component resolution
    public void resolve() throws UnresolvableReferenceException, ParseException {

        if (dataProductVersionRes.getInterfaceComponents() == null) {
            return;
        }

        resolveTemplateObjectsInApp();
        resolveTemplateObjectsInInfra();
    }

    private void resolveTemplateObjectsInApp() throws UnresolvableReferenceException, ParseException {

        List<ApplicationComponentResource> applicationComponents = dataProductVersionRes.getInternalComponents()
                .getApplicationComponents();
        for (ApplicationComponentResource applicationComponent : applicationComponents) {

            ObjectNode applicationNode = null;
            try {
                applicationNode = (ObjectNode) mapper.readTree(applicationComponent.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse descriptor raw cantent", e);
            }

            ObjectNode serviceInfoNode = null;
            ObjectNode templateNode = null;
            ReferenceObjectResource template = null;

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

        List<InfrastructuralComponentResource> infrastructuralComponents = dataProductVersionRes.getInternalComponents()
                .getInfrastructuralComponents();
        for (InfrastructuralComponentResource infrastructuralComponent : infrastructuralComponents) {

            ObjectNode infraNode = null;
            try {
                infraNode = (ObjectNode) mapper.readTree(infrastructuralComponent.getRawContent());
            } catch (JsonProcessingException e) {
                throw new ParseException("Impossible to parse descriptor raw cantent", e);
            }

            ObjectNode serviceInfoNode = null;
            ObjectNode templateNode = null;
            ReferenceObjectResource template = null;

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
            ReferenceObjectResource reference,
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

    

   
    public static void resolve(DataProductVersionResource dataProductVersionRes, DataProductVersionSource source,
            String targetURL) throws UnresolvableReferenceException, ParseException {
        TemplatesResolver resolver = new TemplatesResolver(dataProductVersionRes, source,
                targetURL);
        resolver.resolve();
    }
}
