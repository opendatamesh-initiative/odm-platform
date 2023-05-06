package org.opendatamesh.dpexperience.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

import org.opendatamesh.platform.pp.api.core.DataProductDescriptor;
import org.opendatamesh.platform.pp.api.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.DataProductResource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenDataMeshITRestTemplate extends TestRestTemplate {

    protected String host = "localhost";
    protected String port = "80";

    private ObjectMapper objectMapper;

    public OpenDataMeshITRestTemplate() {
        super();
        objectMapper = DataProductDescriptor.buildObjectMapper();
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

    // ----------------------------------------
    // Definition
    // ----------------------------------------

    public ResponseEntity<Definition> createDefinition(
            String filePath) throws IOException {
        HttpEntity<String> entity = getVersionFileAsHttpEntity(filePath);

        ResponseEntity<Definition> postDefinitionResponse = postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS),
                entity,
                Definition.class);

        return postDefinitionResponse;
    }
}
