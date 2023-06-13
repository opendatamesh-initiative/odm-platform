package org.opendatamesh.dpexperience.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductResource;
import org.opendatamesh.platform.pp.registry.resources.v1.shared.DataProductSourceResource;
import org.opendatamesh.platform.pp.registry.resources.v1.shared.DefinitionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.shared.TemplateResource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class OpenDataMeshITRestTemplate extends TestRestTemplate {

    protected String host = "localhost";
    protected String port = "80";

    private ObjectMapper objectMapper;

    public OpenDataMeshITRestTemplate(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    protected String apiUrl(RoutesV1 route) {
        return apiUrl(route, "");
    }

    protected String apiUrl(RoutesV1 route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    protected String apiUrlFromString(String routeUrlString) {
        return "http://" + host + ":" + port + routeUrlString;
    }

    protected String apiUrlOfItem(RoutesV1 route) {
        return apiUrl(route, "/{id}");
    }

    protected String readFile(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }

    HttpEntity<DataProductResource> getProductDocumentAsHttpEntity(String file)
    throws IOException {

        HttpEntity<DataProductResource> entity = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        DataProductResource dataProductResource = null;
        if(file != null) {
            String docContent = readFile(file);
            dataProductResource = objectMapper.readValue(docContent, DataProductResource.class);
        }

        entity = new HttpEntity<DataProductResource>(dataProductResource, headers);

        return entity;
    }

    HttpEntity<String> getVersionFileAsHttpEntity(String file) throws IOException {
        String descriptor = " ";
        if (file != null) {
            descriptor = readFile(file);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(descriptor, headers);

        return entity;
    }

    HttpEntity<DefinitionResource> getDefinitionAsHttpEntity(String file)
            throws IOException {

        HttpEntity<DefinitionResource> entity = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        DefinitionResource definitionResource = null;
        if(file != null) {
            String docContent = readFile(file);
            definitionResource = objectMapper.readValue(docContent, DefinitionResource.class);
        }

        entity = new HttpEntity<>(definitionResource, headers);

        return entity;
    }

    HttpEntity<TemplateResource> getTemplateAsHttpEntity(String file) throws IOException {

        HttpEntity<TemplateResource> entity = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        TemplateResource templateResource = null;
        if(file != null) {
            String docContent = readFile(file);
            templateResource = objectMapper.readValue(docContent, TemplateResource.class);
        }

        entity = new HttpEntity<>(templateResource, headers);

        return entity;
    }

    HttpEntity<String> getObjectAsHttpEntity(Object o) throws IOException {
        String json = objectMapper.writeValueAsString(o);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);

        return entity;
    }

    // ======================================================================================
    // Proxy services
    // ======================================================================================

    // ----------------------------------------
    // Data product
    // ----------------------------------------

    public ResponseEntity<DataProductResource> createDataProduct(
            String filePath) throws IOException {
        HttpEntity<String> entity = getVersionFileAsHttpEntity(filePath);

        ResponseEntity<DataProductResource> postProductResponse = postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS),
                entity,
                DataProductResource.class);

        return postProductResponse;
    }

    public ResponseEntity<DataProductResource> createDataProduct(
            String fqn, String domain, String description) throws IOException {
        return createDataProduct(null, fqn, domain, description);
    }

    public ResponseEntity<DataProductResource> createDataProduct(String id,
            String fqn, String domain, String description) throws IOException {
        HttpEntity<String> entity = null;

        DataProductResource dataProductRes = null;

        dataProductRes = new DataProductResource();
        dataProductRes.setId(id);
        dataProductRes.setFullyQualifiedName(fqn);
        dataProductRes.setDescription(description);
        dataProductRes.setDomain(domain);
        entity = getObjectAsHttpEntity(dataProductRes);

        ResponseEntity<DataProductResource> postProductResponse = postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS),
                entity,
                DataProductResource.class);

        return postProductResponse;
    }

    public ResponseEntity<DataProductResource> updateDataProduct(
            String filePath) throws IOException {

        HttpEntity<DataProductResource> entity = getProductDocumentAsHttpEntity(filePath);

        ResponseEntity<DataProductResource> putProductResponse = exchange(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/"),
                HttpMethod.PUT,
                entity,
                DataProductResource.class
        );

        return putProductResponse;
    }

    public ResponseEntity<DataProductResource[]> readAllDataProducts() {
        return getForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS),
                DataProductResource[].class);
    }

    public ResponseEntity<DataProductResource> readOneDataProduct(String fqn) {
        return getForEntity(
            apiUrlOfItem(RoutesV1.DATA_PRODUCTS),
            DataProductResource.class,
            UUID.nameUUIDFromBytes(fqn.getBytes()).toString());
    }

    

    // ----------------------------------------
    // Data product version
    // ----------------------------------------

    public ResponseEntity<String> createDataProductVersion(
            String dataProductId, String filePath) throws IOException {

        HttpEntity<String> entity = getVersionFileAsHttpEntity(filePath);

        ResponseEntity<String> postProductVersionResponse = postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                String.class,
                dataProductId);

        return postProductVersionResponse;
    }

    public ResponseEntity<String[]> readAllDataProductVersions(String dataProductId) {
        return getForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                String[].class,
                dataProductId);
    }

    public ResponseEntity<String> readOneDataProductVersion(String dataProductId, String dataProductVersionNumber) {
        return getForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions/{number}"),
                String.class,
                dataProductId,
                dataProductVersionNumber);
    }

    public ResponseEntity<String> deleteDataProductVersion(String dataProductId, String dataProductVersionNumber) {
        return exchange(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions/{number}"),
                HttpMethod.DELETE, null,
                String.class,
                dataProductId, dataProductVersionNumber);
    }

    public ResponseEntity<String> uploadDataProductVersion(
            String uri) throws IOException {
              
        DataProductSourceResource dataProductSourceRes = new DataProductSourceResource();
        dataProductSourceRes.setUri(uri);
        HttpEntity<String> entity = getObjectAsHttpEntity(dataProductSourceRes);

        ResponseEntity<String> postUploadResponse = postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS_UPLOADS),
                entity,
                String.class);

        return postUploadResponse;
    }

    // ----------------------------------------
    // Definition
    // ----------------------------------------

    public ResponseEntity<Definition> createDefinition(
            String filePath) throws IOException {
        HttpEntity<DefinitionResource> entity = getDefinitionAsHttpEntity(filePath);

        ResponseEntity<Definition> postDefinitionResponse = postForEntity(
                apiUrl(RoutesV1.DEFINITIONS),
                entity,
                Definition.class);

        return postDefinitionResponse;
    }

    public ResponseEntity<DefinitionResource[]> readAllDefinitions() {
        return getForEntity(
                apiUrl(RoutesV1.DEFINITIONS),
                DefinitionResource[].class);
    }

    public ResponseEntity<DefinitionResource> readOneDefinition(Long definitionId) {
        return getForEntity(
                apiUrlOfItem(RoutesV1.DEFINITIONS),
                DefinitionResource.class,
                definitionId
        );
    }

    public ResponseEntity<Void> deleteDefinition(Long definitionId) {
        return exchange(
                apiUrlOfItem(RoutesV1.DEFINITIONS),
                HttpMethod.DELETE,
                null,
                Void.class,
                definitionId
        );
    }

    public ResponseEntity<DefinitionResource[]> searchDefinitions(
            Optional<String> name,
            Optional<String> version,
            Optional<String> type,
            Optional<String> specification,
            Optional<String> specificationVersion
    ) {

        Boolean firstParam = true;
        String urlExtensions = "";
        if (name.isPresent()) {
            if(firstParam) {
                urlExtensions = urlExtensions + "?";
                firstParam = false;
            }
            urlExtensions = urlExtensions + "name=" + name.get();
        }
        if (version.isPresent()) {
            if (firstParam) {
                urlExtensions = urlExtensions + "?";
                firstParam = false;
            } else {
                urlExtensions = urlExtensions + "&";
            }
            urlExtensions = urlExtensions + "version=" + version.get();
        }
        if (type.isPresent()) {
            if (firstParam) {
                urlExtensions = urlExtensions + "?";
                firstParam = false;
            } else {
                urlExtensions = urlExtensions + "&";
            }
            urlExtensions = urlExtensions + "type=" + type.get();
        }
        if (specification.isPresent()) {
            if (firstParam) {
                urlExtensions = urlExtensions + "?";
                firstParam = false;
            } else {
                urlExtensions = urlExtensions + "&";
            }
            urlExtensions = urlExtensions + "specification=" + specification.get();
        }
        if (specificationVersion.isPresent()) {
            if (firstParam) {
                urlExtensions = urlExtensions + "?";
            } else {
                urlExtensions = urlExtensions + "&";
            }
            urlExtensions = urlExtensions + "specificationVersion=" + specificationVersion.get();
        }

        return getForEntity(
                apiUrl(RoutesV1.DEFINITIONS, urlExtensions),
                DefinitionResource[].class);
    }


    // ----------------------------------------
    // Template
    // ----------------------------------------

    public ResponseEntity<TemplateResource> createTemplate(String filePath) throws IOException {
        HttpEntity<TemplateResource> entity = getTemplateAsHttpEntity(filePath);
        ResponseEntity<TemplateResource> postTemplateResponse = postForEntity(
                apiUrl(RoutesV1.TEMPLATES),
                entity,
                TemplateResource.class
        );
        return postTemplateResponse;
    }

    public ResponseEntity<TemplateResource[]> readAllTemplates() {
        return getForEntity(
                apiUrl(RoutesV1.TEMPLATES),
                TemplateResource[].class
        );
    }

    public ResponseEntity<TemplateResource> readOneTemplate(Long templateId) {
        return getForEntity(
                apiUrlOfItem(RoutesV1.TEMPLATES),
                TemplateResource.class,
                templateId
        );
    }

    public ResponseEntity<Void> deleteTemplate(Long templateId) {
        return exchange(
                apiUrlOfItem(RoutesV1.TEMPLATES),
                HttpMethod.DELETE,
                null,
                Void.class,
                templateId
        );
    }

    public ResponseEntity<TemplateResource[]> searchTemplates(
            Optional<String> mediaType
    ) {

        String urlExtensions = "";
        if (mediaType.isPresent())
            urlExtensions = urlExtensions + "?mediaType=" + mediaType.get();

        return getForEntity(
                apiUrl(RoutesV1.TEMPLATES, urlExtensions),
                TemplateResource[].class
        );
    }

}
