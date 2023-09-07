package org.opendatamesh.platform.pp.registry.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RegistryClient extends ODMClient {

    public RegistryClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    // ======================================================================================
    // DATA PRODUCT
    // ======================================================================================

    // ----------------------------------------
    // CREATE
    // ----------------------------------------

    public DataProductResource createDataProduct(Object payload) throws IOException {
        return postDataProduct(payload).getBody();
    }

    public ResponseEntity<DataProductResource> postDataProduct(
            Object payload) throws IOException {

        return postDataProduct(payload, DataProductResource.class);
    }

    public <T> ResponseEntity<T> postDataProduct(
            Object payload, Class<T> responseType) throws IOException {

        return rest.postForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS),
                getHttpEntity(payload),
                responseType);
    }

    // ----------------------------------------
    // READ
    // ----------------------------------------

    public DataProductResource[] readAllDataProducts() {
        return getDataProducts().getBody();
    }

    public ResponseEntity<DataProductResource[]> getDataProducts() {
        return getDataProducts(DataProductResource[].class);
    }

    public <T> ResponseEntity<T> getDataProducts(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS),
                responseType);
    }

    public DataProductResource readDataProduct(String id) {
        return getDataProduct(id).getBody();
    }

    public ResponseEntity<DataProductResource> getDataProduct(String id) {
        return getDataProduct(id, DataProductResource.class);
    }

    public <T> ResponseEntity<T> getDataProduct(String id, Class<T> responseType) {

        return rest.getForEntity(
                apiUrlOfItem(RegistryAPIRoutes.DATA_PRODUCTS),
                responseType,
                id);
    }

    public DataProductResource[] searchDataProducts(
            String fqn,
            String domain) {

        return getDataProducts(fqn, domain).getBody();
    }

    public ResponseEntity<DataProductResource[]> getDataProducts(
            String fqn,
            String domain) {

        return getDataProducts(fqn, domain, DataProductResource[].class);
    }

    public <T> ResponseEntity<T> getDataProducts(
            String fqn,
            String domain,
            Class<T> responseType) {

        Map<String, Object> queryParams = new HashMap<String, Object>();
        if (fqn != null)
            queryParams.put("fqn", fqn);
        if (domain != null)
            queryParams.put("domain", domain);

        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS, queryParams),
                responseType,
                queryParams.values().toArray(new Object[0]));
    }

    // ----------------------------------------
    // UPDATE
    // ----------------------------------------

    public DataProductResource updateDataProduct(Object payload) throws IOException {
        return putDataProduct(payload).getBody();
    }

    public ResponseEntity<DataProductResource> putDataProduct(
            Object payload) throws IOException {
        return putDataProduct(payload, DataProductResource.class);
    }

    public <T> ResponseEntity<T> putDataProduct(
            Object payload, Class<T> responseType) throws IOException {

        return rest.exchange(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS),
                HttpMethod.PUT,
                getHttpEntity(payload),
                responseType);
    }

    // ----------------------------------------
    // DELETE
    // ----------------------------------------

    public DataProductResource deleteDataProduct(String id) throws IOException {
        return deleteOneDataProduct(id).getBody();
    }

    public ResponseEntity<DataProductResource> deleteOneDataProduct(
            String id) throws IOException {
        return deleteOneDataProduct(id, DataProductResource.class);
    }

    public <T> ResponseEntity<T> deleteOneDataProduct(
            String id, Class<T> responseType) throws IOException {

        return rest.exchange(
                apiUrlOfItem(RegistryAPIRoutes.DATA_PRODUCTS),
                HttpMethod.DELETE,
                null,
                responseType,
                id);
    }

    // ======================================================================================
    // DATA PRODUCT VERSION
    // ======================================================================================

    public DataProductVersionDPDS createDataProductVersion(String dataProductId, Object payload) throws IOException {
        return postDataProductVersion(dataProductId, payload).getBody();
    }

    public ResponseEntity<DataProductVersionDPDS> postDataProductVersion(
            String dataProductId, Object payload) throws IOException {

        return postDataProduct(payload, DataProductVersionDPDS.class);
    }

    public <T> ResponseEntity<T> postDataProductVersion(
            String dataProductId, Object payload, Class<T> responseType) throws IOException {

        return rest.postForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS, "/{id}/versions"),
                getHttpEntity(payload),
                responseType,
                dataProductId);
    }

    // this endpoint return just an array of version's numbers
    public String[] readAllDataProductVersions(String dataProductId) {
        return getDataProductVersions(dataProductId).getBody();
    }

    public ResponseEntity<String[]> getDataProductVersions(String dataProductId) {
        return getDataProductVersions(dataProductId, String[].class);
    }

    public <T> ResponseEntity<T> getDataProductVersions(String dataProductId, Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS, "/{id}/versions"),
                responseType,
                dataProductId);
    }

    public DataProductVersionDPDS readOneDataProductVersion(String dataProductId, String dataProductVersionNumber) {
        String descriptorContent = getDataProductVersion(dataProductId, dataProductVersionNumber, String.class)
                .getBody();
        DataProductVersionDPDS dpv = null;
        try {
            dpv = ObjectMapperFactory.JSON_MAPPER.readValue(descriptorContent, DataProductVersionDPDS.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return dpv;

        // TODO find how to set default object mapper into resttemplate message
        // converter
        // return getDataProductVersion(dataProductId,
        // dataProductVersionNumber).getBody();
    }

    public ResponseEntity<DataProductVersionDPDS> getDataProductVersion(String dataProductId,
            String dataProductVersionNumber) {
        return getDataProductVersion(dataProductId, dataProductVersionNumber, DataProductVersionDPDS.class);
    }

    public <T> ResponseEntity<T> getDataProductVersion(
            String dataProductId, String dataProductVersionNumber,
            Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS, "/{id}/versions/{number}"),
                responseType,
                dataProductId,
                dataProductVersionNumber);
    }

    public <T> ResponseEntity<T> deleteDataProductVersion(
            String dataProductId, String dataProductVersionNumber,
            Class<T> responseType) {

        return rest.exchange(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS, "/{id}/versions/{number}"),
                HttpMethod.DELETE, null,
                responseType,
                dataProductId, dataProductVersionNumber);
    }

    public <T> ResponseEntity<T> uploadDataProductVersion(
            DataProductDescriptorLocationResource descriptorLocation, Class<T> responseType) throws IOException {

        ResponseEntity<T> postUploadResponse = rest.postForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS_UPLOADS),
                getHttpEntity(mapper.writeValueAsString(descriptorLocation)),
                responseType);

        return postUploadResponse;
    }


    public ResponseEntity getDataProductVersionApplicationComponents(
            String id,
            String version) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCT_VERSIONS_APPLICATIONS),
                Object.class,
                id,
                version);

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                ApplicationComponentDPDS[].class);
    }

    public ResponseEntity getDataProductVersionInfrastructuralComponents(
            String id,
            String version) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCT_VERSIONS_INFRASTRUCTURES),
                Object.class,
                id,
                version);

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                InfrastructuralComponentDPDS[].class);
    }

    // ----------------------------------------
    // API Definition
    // ----------------------------------------

    public DefinitionResource createApiDefinition(Object payload) throws IOException {
        return postApiDefinition(payload).getBody();
    }

    public ResponseEntity<DefinitionResource> postApiDefinition(
            Object payload) throws IOException {

        return postApiDefinition(payload, DefinitionResource.class);
    }

    public <T> ResponseEntity<T> postApiDefinition(
            Object payload, Class<T> responseType) throws IOException {

        return rest.postForEntity(
                apiUrl(RegistryAPIRoutes.APIS),
                getHttpEntity(payload),
                responseType);
    }

    public ResponseEntity<DefinitionResource[]> readAllApiDefinitions() {
        return getApiDefinitions(DefinitionResource[].class);
    }

    public <T> ResponseEntity<T> getApiDefinitions(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.APIS),
                responseType);
    }

    public ResponseEntity<DefinitionResource> readOneApiDefinition(Long definitionId) {
        return rest.getForEntity(
                apiUrlOfItem(RegistryAPIRoutes.APIS),
                DefinitionResource.class,
                definitionId);
    }

    public <T> ResponseEntity<T> deleteApiDefinition(Long definitionId, Class<T> responseType) {
        return rest.exchange(
                apiUrlOfItem(RegistryAPIRoutes.APIS),
                HttpMethod.DELETE,
                null,
                responseType,
                definitionId);
    }

    public ResponseEntity<DefinitionResource[]> searchApiDefinitions(
            Optional<String> name,
            Optional<String> version,
            Optional<String> type,
            Optional<String> specification,
            Optional<String> specificationVersion) {

        Boolean firstParam = true;
        String urlExtensions = "";
        if (name.isPresent()) {
            if (firstParam) {
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

        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.APIS, urlExtensions),
                DefinitionResource[].class);
    }

    // ----------------------------------------
    // Template Definition
    // ----------------------------------------

    public DefinitionResource createTemplateDefinition(Object payload) throws IOException {
        return postTemplateDefinition(payload).getBody();
    }

    public ResponseEntity<DefinitionResource> postTemplateDefinition(
            Object payload) throws IOException {

        return postTemplateDefinition(payload, DefinitionResource.class);
    }

    public <T> ResponseEntity<T> postTemplateDefinition(
            Object payload, Class<T> responseType) throws IOException {

        return rest.postForEntity(
                apiUrl(RegistryAPIRoutes.TEMPLATES),
                getHttpEntity(payload),
                responseType);
    }

    public ResponseEntity<DefinitionResource[]> readAllTemplateDefinitions() {
        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.TEMPLATES),
                DefinitionResource[].class);
    }

    public DefinitionResource readOneTemplateDefinition(Long definitionId) {
        return getOneTemplateDefinition(definitionId).getBody();
    }

    public ResponseEntity<DefinitionResource> getOneTemplateDefinition(Long definitionId) {
        return getOneTemplateDefinition(definitionId, DefinitionResource.class);
    }

    public <T> ResponseEntity<T> getOneTemplateDefinition(Long definitionId, Class<T> responseType) {
        return rest.getForEntity(
                apiUrlOfItem(RegistryAPIRoutes.TEMPLATES),
                responseType,
                definitionId);
    }

    public <T> ResponseEntity<T> deleteTemplateDefinition(Long definitionId, Class<T> responseType) {
        return rest.exchange(
                apiUrlOfItem(RegistryAPIRoutes.TEMPLATES),
                HttpMethod.DELETE,
                null,
                responseType,
                definitionId);
    }

    public ResponseEntity<DefinitionResource[]> searchTemplateDefinitions(
            Optional<String> name,
            Optional<String> version,
            Optional<String> type,
            Optional<String> specification,
            Optional<String> specificationVersion) {

        Boolean firstParam = true;
        String urlExtensions = "";
        if (name.isPresent()) {
            if (firstParam) {
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

        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.TEMPLATES, urlExtensions),
                DefinitionResource[].class);
    }

    // ----------------------------------------
    // Schema
    // ----------------------------------------

    public SchemaResource createSchema(Object payload) throws IOException {
        return postSchema(payload).getBody();
    }

    public ResponseEntity<SchemaResource> postSchema(
            Object payload) throws IOException {

        return postSchema(payload, SchemaResource.class);
    }

    public <T> ResponseEntity<T> postSchema(
            Object payload, Class<T> responseType) throws IOException {

        return rest.postForEntity(
                apiUrl(RegistryAPIRoutes.SCHEMAS),
                getHttpEntity(payload),
                responseType);
    }

    public ResponseEntity readSchemas() throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.SCHEMAS),
                Object.class);

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                SchemaResource[].class);
    }

    public ResponseEntity getSchemaById(Long id) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrlOfItem(RegistryAPIRoutes.SCHEMAS),
                Object.class,
                id);

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                SchemaResource.class);

    }

    public ResponseEntity getSchemaContentById(Long id) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.SCHEMAS, "/{id}/raw"),
                Object.class,
                id);

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                String.class);

    }

    public ResponseEntity getSchemaApiRelationshipById(Long id) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.SCHEMAS, "/{id}/apis"),
                Object.class,
                id);

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                ApiToSchemaRelationshipResource[].class);

    }

    public ResponseEntity deleteSchema(Long id) throws JsonProcessingException {

        ResponseEntity deleteResponse = rest.exchange(
                apiUrlOfItem(RegistryAPIRoutes.SCHEMAS),
                HttpMethod.DELETE,
                null,
                Object.class,
                id);

        return mapResponseEntity(
                deleteResponse,
                HttpStatus.OK,
                Void.class);

    }

    // ----------------------------------------
    // Data Product Components
    // ----------------------------------------

    public ResponseEntity getDataProductPorts(String dataProductId, String versionId) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS, "/{id}/versions/{version}/ports"),
                Object.class,
                dataProductId,
                versionId);

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                String.class);

    }

    // ----------------------------------------
    // Utils
    // ----------------------------------------

    protected ResponseEntity mapResponseEntity(
            ResponseEntity response,
            HttpStatus acceptedStatusCode,
            Class acceptedClass) throws JsonProcessingException {
        return mapResponseEntity(response, List.of(acceptedStatusCode), acceptedClass, ErrorRes.class);
    }

}
