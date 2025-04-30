package org.opendatamesh.platform.pp.registry.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.internals.ApplicationComponentDPDS;
import org.opendatamesh.dpds.model.internals.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
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
            dpv.setRawContent(descriptorContent);
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
                InfrastructuralComponentDPDS[].class
        );

    }

    // ----------------------------------------
    // API Definition
    // ----------------------------------------

    public ExternalComponentResource createApi(Object payload) throws IOException {
        return postApi(payload).getBody();
    }

    public ResponseEntity<ExternalComponentResource> postApi(
            Object payload) throws IOException {

        return postApi(payload, ExternalComponentResource.class);
    }

    public <T> ResponseEntity<T> postApi(
            Object payload, Class<T> responseType) throws IOException {

        return rest.postForEntity(
                apiUrl(RegistryAPIRoutes.APIS),
                getHttpEntity(payload),
                responseType);
    }

    public ExternalComponentResource[] readAllApis() {
        return getApis().getBody();
    }

    public ResponseEntity<ExternalComponentResource[]> getApis() {
        return getApis(ExternalComponentResource[].class);
    }

    public <T> ResponseEntity<T> getApis(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.APIS),
                responseType);
    }

    public ExternalComponentResource readApi(String id) {
        return getApi(id).getBody();
    }

    public ResponseEntity<ExternalComponentResource> getApi(String id) {
        return getApi(id, ExternalComponentResource.class);
    }

    public <T> ResponseEntity<T> getApi(String id, Class<T> responseType) {
        return rest.getForEntity(
                apiUrlOfItem(RegistryAPIRoutes.APIS),
                responseType,
                id);
    }

    public ExternalComponentResource deleteApi(String id) throws IOException {
        return deleteOneApi(id).getBody();
    }

    public ResponseEntity<ExternalComponentResource> deleteOneApi(
            String id) throws IOException {
        return deleteApi(id, ExternalComponentResource.class);
    }

    public <T> ResponseEntity<T> deleteApi(String apiId, Class<T> responseType) {
        return rest.exchange(
                apiUrlOfItem(RegistryAPIRoutes.APIS),
                HttpMethod.DELETE,
                null,
                responseType,
                apiId);
    }

    public ResponseEntity<ExternalComponentResource[]> searchApis(
            Optional<String> name,
            Optional<String> version,
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
                ExternalComponentResource[].class);
    }

    // ----------------------------------------
    // Template Definition
    // ----------------------------------------

    public ExternalComponentResource createTemplate(Object payload) throws IOException {
        return postTemplate(payload).getBody();
    }

    public ResponseEntity<ExternalComponentResource> postTemplate(
            Object payload) throws IOException {

        return postTemplate(payload, ExternalComponentResource.class);
    }

    public <T> ResponseEntity<T> postTemplate(
            Object payload, Class<T> responseType) throws IOException {

        return rest.postForEntity(
                apiUrl(RegistryAPIRoutes.TEMPLATES),
                getHttpEntity(payload),
                responseType);
    }

    public ExternalComponentResource[] readAllTemplates() {
        return getTemplates().getBody();
    }

    public ResponseEntity<ExternalComponentResource[]> getTemplates() {
        return getTemplates(ExternalComponentResource[].class);
    }

    public <T> ResponseEntity<T> getTemplates(Class<T> responseType) {
        return rest.getForEntity(
                apiUrl(RegistryAPIRoutes.TEMPLATES),
                responseType);
    }


    public ExternalComponentResource readTemplate(String id) {
        return getTemplate(id).getBody();
    }

    public ResponseEntity<ExternalComponentResource> getTemplate(String id) {
        return getTemplate(id, ExternalComponentResource.class);
    }

    public <T> ResponseEntity<T> getTemplate(String id, Class<T> responseType) {
        return rest.getForEntity(
                apiUrlOfItem(RegistryAPIRoutes.TEMPLATES),
                responseType,
                id);
    }

    public ExternalComponentResource deleteTemplate(String id) {
        return deleteOneTemplate(id).getBody();
    }

    public ResponseEntity<ExternalComponentResource> deleteOneTemplate(
            String id) {
        return deleteTemplate(id, ExternalComponentResource.class);
    }


    public <T> ResponseEntity<T> deleteTemplate(String id, Class<T> responseType) {
        return rest.exchange(
                apiUrlOfItem(RegistryAPIRoutes.TEMPLATES),
                HttpMethod.DELETE,
                null,
                responseType,
                id);
    }

    public ResponseEntity<ExternalComponentResource[]> searchTemplates(
            Optional<String> name,
            Optional<String> version,
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
                ExternalComponentResource[].class);
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

    // ======================================================================================
    // DOMAINS
    // ======================================================================================

    // ----------------------------------------
    // CREATE
    // ----------------------------------------

    public ResponseEntity createDomain(
            Object payload) throws IOException {

        ResponseEntity postNotificationResponse = rest.postForEntity(
                apiUrl(RegistryAPIRoutes.DOMAINS),
                getHttpEntity(payload),
                Object.class);

        ResponseEntity response = mapResponseEntity(postNotificationResponse,
                HttpStatus.CREATED,
                DomainResource.class);
        return response;
    }

    // ----------------------------------------
    // READ
    // ----------------------------------------

    public ResponseEntity readAllDomains() throws JsonProcessingException {

        ResponseEntity getDomainResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DOMAINS),
                Object.class);

        ResponseEntity response = mapResponseEntity(getDomainResponse,
                HttpStatus.OK,
                DomainResource[].class);
        return response;
    }

    public ResponseEntity getDomainById(String id) throws IOException {

        ResponseEntity getResponse =  rest.getForEntity(
                apiUrlOfItem(RegistryAPIRoutes.DOMAINS),
                Object.class,
                id
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                DomainResource.class
        );
    }


    // ----------------------------------------
    // UPDATE
    // ----------------------------------------

    public ResponseEntity updateDomain(DomainResource domain) throws IOException {
        ResponseEntity putPolicyResponse = rest.exchange(
                apiUrl(RegistryAPIRoutes.DOMAINS),
                HttpMethod.PUT,
                getHttpEntity(domain),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(putPolicyResponse,
                HttpStatus.OK,
                DomainResource.class);
        return response;
    }

    // ----------------------------------------
    // DELETE
    // ----------------------------------------

    public ResponseEntity deleteDomain(String id) throws JsonProcessingException {

        ResponseEntity deleteResponse = rest.exchange(
                apiUrlOfItem(RegistryAPIRoutes.DOMAINS),
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

    // ======================================================================================
    // OWNER
    // ======================================================================================

    // ----------------------------------------
    // CREATE
    // ----------------------------------------

    public ResponseEntity createOwner(
            Object payload) throws IOException {

        ResponseEntity postNotificationResponse = rest.postForEntity(
                apiUrl(RegistryAPIRoutes.OWNERS),
                getHttpEntity(payload),
                Object.class);

        ResponseEntity response = mapResponseEntity(postNotificationResponse,
                HttpStatus.CREATED,
                OwnerResource.class);
        return response;
    }

    // ----------------------------------------
    // READ
    // ----------------------------------------

    public ResponseEntity readAllOwners() throws JsonProcessingException {

        ResponseEntity getDomainResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.OWNERS),
                Object.class);

        ResponseEntity response = mapResponseEntity(getDomainResponse,
                HttpStatus.OK,
                OwnerResource[].class);
        return response;
    }

    public ResponseEntity getOwnerById(String id) throws IOException {

        ResponseEntity getResponse =  rest.getForEntity(
                apiUrlOfItem(RegistryAPIRoutes.OWNERS),
                Object.class,
                id
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                OwnerResource.class
        );
    }


    // ----------------------------------------
    // UPDATE
    // ----------------------------------------

    public ResponseEntity updateOwner(OwnerResource owner) throws IOException {
        ResponseEntity putPolicyResponse = rest.exchange(
                apiUrl(RegistryAPIRoutes.OWNERS),
                HttpMethod.PUT,
                getHttpEntity(owner),
                Object.class
        );

        ResponseEntity response = mapResponseEntity(putPolicyResponse,
                HttpStatus.OK,
                OwnerResource.class);
        return response;
    }

    // ----------------------------------------
    // DELETE
    // ----------------------------------------

    public ResponseEntity deleteOwner(String id) throws JsonProcessingException {

        ResponseEntity deleteResponse = rest.exchange(
                apiUrlOfItem(RegistryAPIRoutes.OWNERS),
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


    // ======================================================================================
    // VARIABLE
    // ======================================================================================


    // ----------------------------------------
    // READ
    // ----------------------------------------

    public ResponseEntity readDataProductVersionVariables(
            String dataProductId, String versionNumber
    ) throws JsonProcessingException {

        ResponseEntity getResponse = rest.getForEntity(
                apiUrl(RegistryAPIRoutes.DATA_PRODUCTS, "/{id}/versions/{version}/variables"),
                Object.class,
                dataProductId,
                versionNumber
        );

        return mapResponseEntity(
                getResponse,
                HttpStatus.OK,
                VariableResource[].class
        );
    }

    // ----------------------------------------
    // UPDATE
    // ----------------------------------------

    public ResponseEntity updateVariable(
            String dataProductId, String versionNumber, Long varId, String variableValue
    ) throws JsonProcessingException {

        ResponseEntity putVariableResponse = rest.exchange(
                apiUrl(
                        RegistryAPIRoutes.DATA_PRODUCTS,
                        "/{id}/versions/{version}/variables/{varId}?value={value}"
                ),
                HttpMethod.PUT,
                null,
                Object.class,
                dataProductId,
                versionNumber,
                varId,
                variableValue
        );

        return mapResponseEntity(
                putVariableResponse,
                HttpStatus.OK,
                VariableResource.class
        );

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
