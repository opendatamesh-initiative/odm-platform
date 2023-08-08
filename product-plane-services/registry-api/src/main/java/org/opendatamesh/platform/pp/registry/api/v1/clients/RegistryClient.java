package org.opendatamesh.platform.pp.registry.api.v1.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.v1.resources.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RegistryClient extends ODMClient {

    public RegistryClient(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    // ======================================================================================
    // Proxy services
    // ======================================================================================

    // ----------------------------------------
    // Data product
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
                apiUrl(Routes.DATA_PRODUCTS),
                getHttpEntity(payload),
                responseType);
    }

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
                apiUrl(Routes.DATA_PRODUCTS, "/"),
                HttpMethod.PUT,
                getHttpEntity(payload),
                responseType);
    }

    public DataProductResource[] readAllDataProducts() {
        return getDataProducts().getBody();
    }

    public ResponseEntity<DataProductResource[]> getDataProducts() {
        return getDataProducts(DataProductResource[].class);
    }

    public <T> ResponseEntity<T> getDataProducts(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(Routes.DATA_PRODUCTS),
                responseType);
    }

    public DataProductResource findDataProductByFqn(String fqn) {
        return getDataProductByFqn(fqn).getBody();
    }

    public ResponseEntity<DataProductResource> getDataProductByFqn(String fqn) {
        return getDataProductByFqn(fqn, DataProductResource.class);
    }

    public <T> ResponseEntity<T> getDataProductByFqn(String fqn, Class<T> responseType) {
        return rest.getForEntity(
                apiUrlOfItem(Routes.DATA_PRODUCTS),
                responseType,
                UUID.nameUUIDFromBytes(fqn.getBytes()).toString());
    }

    public ResponseEntity getDataProductById(String id) throws JsonProcessingException {

        ResponseEntity getResponse =  rest.getForEntity(
                apiUrlOfItem(Routes.DATA_PRODUCTS),
                Object.class,
                id
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                DataProductResource.class
        );

    }

    public ResponseEntity deleteDataProduct(String id) throws JsonProcessingException {

        ResponseEntity deleteResponse = rest.exchange(
                apiUrlOfItem(Routes.DATA_PRODUCTS),
                HttpMethod.DELETE,
                null,
                Object.class,
                id
        );

        return mapResponseEntity(
                deleteResponse,
                HttpStatus.OK,
                Void.class
        );

    }


    // ----------------------------------------
    // Data product version
    // ----------------------------------------

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
                apiUrl(Routes.DATA_PRODUCTS, "/{id}/versions"),
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
                apiUrl(Routes.DATA_PRODUCTS, "/{id}/versions"),
                responseType,
                dataProductId);
    }

    public DataProductVersionDPDS readOneDataProductVersion(String dataProductId, String dataProductVersionNumber) {
        String descriptorContent = getDataProductVersion(dataProductId, dataProductVersionNumber, String.class).getBody();
        DataProductVersionDPDS dpv = null;
        try {
			dpv = ObjectMapperFactory.JSON_MAPPER.readValue(descriptorContent, DataProductVersionDPDS.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        return dpv;

        // TODO find how to set default object mapper into resttemplate message converter
        //return getDataProductVersion(dataProductId, dataProductVersionNumber).getBody();
    }

    public ResponseEntity<DataProductVersionDPDS> getDataProductVersion(String dataProductId, String dataProductVersionNumber) {
        return getDataProductVersion(dataProductId, dataProductVersionNumber, DataProductVersionDPDS.class);
    }

    public <T> ResponseEntity<T> getDataProductVersion(
            String dataProductId, String dataProductVersionNumber,
            Class<T> responseType)
    {
        return rest.getForEntity(
                apiUrl(Routes.DATA_PRODUCTS, "/{id}/versions/{number}"),
                responseType,
                dataProductId,
                dataProductVersionNumber);
    }

    public <T> ResponseEntity<T> deleteDataProductVersion(
            String dataProductId, String dataProductVersionNumber,
            Class<T> responseType)
    {

        return rest.exchange(
                apiUrl(Routes.DATA_PRODUCTS, "/{id}/versions/{number}"),
                HttpMethod.DELETE, null,
                responseType,
                dataProductId, dataProductVersionNumber);
    }


    public <T> ResponseEntity<T> uploadDataProductVersion(
            DataProductDescriptorLocationResource descriptorLocation, Class<T> responseType) throws IOException {

        ResponseEntity<T> postUploadResponse = rest.postForEntity(
                apiUrl(Routes.DATA_PRODUCTS_UPLOADS),
                getHttpEntity( mapper.writeValueAsString(descriptorLocation) ),
                responseType);

        return postUploadResponse;
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
                apiUrl(Routes.APIS),
                getHttpEntity(payload),
                responseType);
    }

    public ResponseEntity<DefinitionResource[]> readAllApiDefinitions() {
        return getApiDefinitions(DefinitionResource[].class);
    }

    public <T> ResponseEntity<T> getApiDefinitions(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(Routes.APIS),
                responseType);
    }

    public ResponseEntity<DefinitionResource> readOneApiDefinition(Long definitionId) {
        return rest.getForEntity(
                apiUrlOfItem(Routes.APIS),
                DefinitionResource.class,
                definitionId);
    }

    public <T> ResponseEntity<T> deleteApiDefinition(Long definitionId, Class<T> responseType) {
        return rest.exchange(
                apiUrlOfItem(Routes.APIS),
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
                apiUrl(Routes.APIS, urlExtensions),
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
                apiUrl(Routes.TEMPLATES),
                getHttpEntity(payload),
                responseType);
    }

    public ResponseEntity<DefinitionResource[]> readAllTemplateDefinitions() {
        return rest.getForEntity(
                apiUrl(Routes.TEMPLATES),
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
                apiUrlOfItem(Routes.TEMPLATES),
                responseType,
                definitionId);
    }



    public <T> ResponseEntity<T> deleteTemplateDefinition(Long definitionId, Class<T> responseType) {
        return rest.exchange(
                apiUrlOfItem(Routes.TEMPLATES),
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
                apiUrl(Routes.TEMPLATES, urlExtensions),
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
                apiUrl(Routes.SCHEMAS),
                getHttpEntity(payload),
                responseType);
    }

    public ResponseEntity readSchemas() throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(Routes.SCHEMAS),
                Object.class
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                SchemaResource[].class
        );
    }

    public ResponseEntity getSchemaById(Long id) throws JsonProcessingException {

        ResponseEntity getResponse =  rest.getForEntity(
                apiUrlOfItem(Routes.SCHEMAS),
                Object.class,
                id
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                SchemaResource.class
        );

    }

    public ResponseEntity getSchemaContentById(Long id) throws JsonProcessingException {

        ResponseEntity getResponse =  rest.getForEntity(
                apiUrl(Routes.SCHEMAS, "/{id}/raw"),
                Object.class,
                id
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                String.class
        );

    }

    public ResponseEntity getSchemaApiRelationshipById(Long id) throws JsonProcessingException {

        ResponseEntity getResponse =  rest.getForEntity(
                apiUrl(Routes.SCHEMAS, "/{id}/apis"),
                Object.class,
                id
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                ApiToSchemaRelationshipResource[].class
        );

    }

    public ResponseEntity deleteSchema(Long id) throws JsonProcessingException {

        ResponseEntity deleteResponse = rest.exchange(
                apiUrlOfItem(Routes.SCHEMAS),
                HttpMethod.DELETE,
                null,
                Object.class,
                id
        );

        return mapResponseEntity(
                deleteResponse,
                HttpStatus.OK,
                Void.class
        );

    }


    // ----------------------------------------
    // Data Product Components
    // ----------------------------------------

    public ResponseEntity getDataProductPorts(String dataProductId, String versionId) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(Routes.DATA_PRODUCTS, "/{id}/versions/{version}/ports"),
                Object.class,
                dataProductId,
                versionId
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                String.class
        );

    }


    // ----------------------------------------
    // Utils
    // ----------------------------------------

    protected ResponseEntity mapResponseEntity(
            ResponseEntity response,
            HttpStatus acceptedStatusCode,
            Class acceptedClass
    ) throws JsonProcessingException {
        return mapResponseEntity(response, List.of(acceptedStatusCode), acceptedClass, ErrorRes.class);
    }

}
