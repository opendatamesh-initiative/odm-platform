package org.opendatamesh.dpexperience.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.opendatamesh.platform.pp.registry.core.CoreApp;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InfrastructuralComponentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

//@@FixMethodOrder(MethodSorters.JVM)
@TestPropertySource(properties = { "spring.test.context.parallel.enabled=false" })
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductVersionIT extends OpenDataMeshIT {

    // private MockRestServiceServer mockServer;

    InfrastructuralComponentResource infrastructuralComponent;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${policyserviceaddress}")
    private String policyserviceaddress;
    @Value("${metaserviceaddress}")
    private String metaserviceaddress;

    // JsonNode descriptorContent;

    @Before
    public void setup() {
        //objectMapper = DataProductDescriptor.buildObjectMapper();
        /*
         * mockServer =
         * MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
         * try {
         * mockPolicyServicePOST(true);
         * mockMetaServicePOST();
         * } catch (JsonProcessingException e) {
         * logger.error("Impossible to setup mock server", e);
         * }
         * 
         * idDataProductV1 =
         * UUID.nameUUIDFromBytes(fqnDataProductV1.getBytes()).toString();
         * idDataProductV2 =
         * UUID.nameUUIDFromBytes(fqnDataProductV2.getBytes()).toString();
         * fqnDataProductV1InputPort.add(
         * "urn:dpds:it.quantyca:dataproducts:SampleDP:1:inputports:tmsTripCDC");
         * fqnDataProductV1InputPort.add(
         * "urn:dpds:it.quantyca:dataproducts:SampleDP:1:inputports:input-port-2");
         * fqnDataProductV1OutputPort
         * .add("urn:dpds:it.quantyca:dataproducts:SampleDP:1:outputports:output-port-1"
         * );
         * 
         */
    }

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Data product version
    // ----------------------------------------
    @Test
    @Order(1)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionCreation() throws IOException {

        DataProductResource dataProduct1Res = createDataProduct1();
        // Note: we do not do more tests on data product here because they are
        // alredy done in DataProductIT

        String descriptorContent = createDataProduct1Version1(dataProduct1Res.getId());
        System.out.println(descriptorContent);
        verifyBasicContent(descriptorContent);
    }

    // ----------------------------------------
    // READ Data product version
    // ----------------------------------------
    @Test
    @Order(2)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsReadAll() throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct1();
        String descriptorContent = createDataProduct1Version1(dataProduct1Res.getId());
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        // read all version associated to the created data product
        ResponseEntity<String[]> getDataProductVersionsResponse = rest.readAllDataProductVersions(
                dataProduct1Res.getId());
        verifyResponseEntity(getDataProductVersionsResponse, HttpStatus.OK, true);

        // test response
        String[] dataProductVersionNumbers = getDataProductVersionsResponse.getBody();
        assertThat(dataProductVersionNumbers.length).isEqualTo(1);
        assertThat(dataProductVersionNumbers[0]).isEqualTo(versionNumber);
    }

    @Test
    @Order(3)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsReadOne() throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct1();
        String descriptorContent = createDataProduct1Version1(dataProduct1Res.getId());
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        // read the specific version just created
        ResponseEntity<String> getDataProductVersionResponse = rest.readOneDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber);

        // test response
        verifyResponseEntity(getDataProductVersionResponse, HttpStatus.OK, true);
        verifyBasicContent(getDataProductVersionResponse.getBody());
    }

    // ----------------------------------------
    // UPDATE Data product version
    // ----------------------------------------

    // Data product versions are immutable

    // ----------------------------------------
    // Delete Data product version
    // ----------------------------------------

    @Test
    @Order(4)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionDelete()
            throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct1();
        String descriptorContent = createDataProduct1Version1(dataProduct1Res.getId());
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        ResponseEntity<String> deleteVersionResponse = rest.deleteDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber);
        verifyResponseEntity(deleteVersionResponse, HttpStatus.OK, false);

        ResponseEntity<String> getOneDataProductVersionResponse = rest.readOneDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber);
        verifyResponseEntity(getOneDataProductVersionResponse, HttpStatus.NOT_FOUND, false);

        ResponseEntity<String[]> getAllDataProductVersionsResponse = rest.readAllDataProductVersions(
                dataProduct1Res.getId());
        verifyResponseEntity(getAllDataProductVersionsResponse, HttpStatus.OK, true);

        String[] dataProductVersionNumbers = getAllDataProductVersionsResponse.getBody();
        assertThat(dataProductVersionNumbers.length).isEqualTo(0);
    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Data product version
    // ----------------------------------------    }

    @Test
    @Order(5)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreation400Errors() throws IOException {

        DataProductResource dataProduct1Res = createDataProduct1();

        HttpEntity<String> entity = null;

        // Test error SC400_07_PRODUCT_ID_IS_EMPTY
        // path parameter cannot be null by default. This exception is nerver thrown

        // Test error SC400_01_DESCRIPTOR_IS_EMPTY
        entity = rest.getVersionFileAsHttpEntity(null);
        ResponseEntity<ErrorRes> emptyVersionResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                ErrorRes.class,
                dataProduct1Res.getId());
        verifyResponseError(emptyVersionResponse,
                HttpStatus.BAD_REQUEST, OpenDataMeshAPIStandardError.SC400_01_DESCRIPTOR_IS_EMPTY);
    }

    @Test
    @Order(6)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreation422Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse;
        HttpEntity<String> entity = null;

        // create the product
        DataProductResource dataProduct1Res = createDataProduct1();

        // Test error SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID

        entity = rest.getVersionFileAsHttpEntity(RESOURCE_DP1_V1);

        // remove from descriptor fullyQualifiedName property
        String descriptorContent = entity.getBody();
        JsonNode descriptorRootEntity = mapper.readTree(descriptorContent);
        ObjectNode infoObject = (ObjectNode) descriptorRootEntity.get("info");
        infoObject.remove("fullyQualifiedName");
        entity = new HttpEntity<String>(mapper.writeValueAsString(descriptorRootEntity), entity.getHeaders());

        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                ErrorRes.class,
                dataProduct1Res.getId());
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID);

        // Test error SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID
        entity = new HttpEntity<String>("This us an invalid JSON document", entity.getHeaders());
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                ErrorRes.class,
                dataProduct1Res.getId());
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID);

        // Test error SC422_05_VERSION_ALREADY_EXISTS

        // associate the version to the product
        String str = createDataProduct1Version1(dataProduct1Res.getId());

        // and the re associate it
        entity = rest.getVersionFileAsHttpEntity(RESOURCE_DP1_V1);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                ErrorRes.class,
                dataProduct1Res.getId());

        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_05_VERSION_ALREADY_EXISTS);
    }

    // ======================================================================================
    // PRIVATE METHODS
    // ======================================================================================

    // ----------------------------------------
    // Create test resources
    // ----------------------------------------

    // TODO ...as needed

    // ----------------------------------------
    // Verify test resources
    // ----------------------------------------

    private JsonNode verifyBasicContent(String responseBody) {

        JsonNode rootEntity = verifyJsonSynatx(responseBody);

        // test dataProductDescriptor
        assertThat(rootEntity.get("dataProductDescriptor").asText())
                .isEqualTo("1.0.0");

        // test info object
        JsonNode infoObject = rootEntity.path("info");
        assertThat(infoObject).isNotNull();

        assertThat(infoObject.get("id")).isNotNull();

        assertThat(infoObject.get("entityType")).isNotNull();
        assertThat(infoObject.get("entityType").asText())
                .isEqualTo("dataproduct");

        assertThat(infoObject.get("x-prop")).isNotNull();
        assertThat(infoObject.get("x-prop").asText())
                .isEqualTo("custom-prop-value");

        // test interface components
        JsonNode interfaceComponentsObject = rootEntity.path("interfaceComponents");
        assertThat(interfaceComponentsObject).isNotNull();

        JsonNode inputPorts = interfaceComponentsObject.path("inputPorts");
        assertThat(inputPorts).isNotNull();
        assertThat(inputPorts.isArray()).isTrue();
        assertThat(inputPorts.size()).isEqualTo(2);
        for (JsonNode inputPort : inputPorts) {
            assertThat(inputPort.get("id"))
                    .isNotNull();
            assertThat(inputPort.get("entityType").asText())
                    .isEqualTo("inputport");
            assertThat(inputPort.get("x-prop").asText())
                    .isEqualTo("custom-prop-value");

            ObjectNode apiObject = (ObjectNode) inputPort.at("/promises/api");
            assertThat(apiObject.get("specification")).isNotNull();
            assertThat(apiObject.get("specification").asText()).isEqualTo("custom-api-spec");
            ObjectNode apiDefinitionObject = (ObjectNode) apiObject.get("definition");
            assertThat(apiDefinitionObject.size()).isEqualTo(1);
            assertThat(apiDefinitionObject.get("$ref")).isNotNull();

            /*
             * ResponseEntity<String> getApiDefinitionResponse = rest.getForEntity(
             * apiDefinitionObject.get("$ref").asText(), String.class
             * );
             * assertThat(getApiDefinitionResponse.getStatusCode())
             * .isEqualByComparingTo(HttpStatus.OK);
             * assertThat(getApiDefinitionResponse.getBody())
             * .isNotNull();
             */

        }

        JsonNode outputPorts = interfaceComponentsObject.path("outputPorts");
        assertThat(outputPorts).isNotNull();
        assertThat(outputPorts.isArray()).isTrue();
        assertThat(outputPorts.size()).isEqualTo(1);
        for (JsonNode outputPort : outputPorts) {
            assertThat(outputPort.get("id"))
                    .isNotNull();
            assertThat(outputPort.get("entityType").asText())
                    .isEqualTo("outputport");
            assertThat(outputPort.get("x-prop").asText())
                    .isEqualTo("custom-prop-value");

            ObjectNode apiObject = (ObjectNode) outputPort.at("/promises/api");
            assertThat(apiObject.get("specification")).isNotNull();
            assertThat(apiObject.get("specification").asText()).isEqualTo("custom-api-spec");
            ObjectNode apiDefinitionObject = (ObjectNode) apiObject.get("definition");
            assertThat(apiDefinitionObject.size()).isEqualTo(1);
            assertThat(apiDefinitionObject.get("$ref")).isNotNull();
        }

        // test internal components
        JsonNode internalComponentsObject = rootEntity.path("internalComponents");
        assertThat(internalComponentsObject).isNotNull();

        // test application components
        JsonNode appComponents = internalComponentsObject.path("applicationComponents");
        assertThat(appComponents).isNotNull();
        assertThat(appComponents.isArray()).isTrue();
        assertThat(appComponents.size()).isEqualTo(1);
        for (JsonNode appComponent : appComponents) {
            assertThat(appComponent.get("id"))
                    .isNotNull();
            assertThat(appComponent.get("entityType").asText())
                    .isEqualTo("application");
            assertThat(appComponent.get("x-prop").asText())
                    .isEqualTo("custom-prop-value");
        }

        // test application components
        JsonNode infraComponents = internalComponentsObject.path("infrastructuralComponents");
        assertThat(infraComponents).isNotNull();
        assertThat(infraComponents.isArray()).isTrue();
        assertThat(infraComponents.size()).isEqualTo(1);
        for (JsonNode infraComponent : infraComponents) {
            assertThat(infraComponent.get("id"))
                    .isNotNull();
            assertThat(infraComponent.get("entityType").asText())
                    .isEqualTo("infrastructure");
            assertThat(infraComponent.get("x-prop").asText())
                    .isEqualTo("custom-prop-value");
        }

        return rootEntity;
    }

    // ======================================================================================
    // DEPRECATED TESTS
    // ======================================================================================

    // Creates a data product, searches it by ID, verifies internal components'
    // correctness, deletes it and verifies that it has been correctly deleted
    
    /**
     * @Ignore
     * 
     * @Test
     * public void createLifecycle() throws IOException {
     * logger.debug("*** createLifecycle - POST ***");
     * mockPolicyServicePOST(true);
     * mockMetaServicePOST();
     * try {
     * String dataproductJson = readFile(RESOURCE_DP_DPD_COMPONENTS);
     * 
     * ResponseEntity<DataProductVersionResource> postResponse = rest.postForEntity(
     * apiUrl(RoutesV1.DATA_PRODUCTS_UPLOADS),
     * dataproductJson, DataProductVersionResource.class);
     * DataProductVersionResource obtainedDataProduct = postResponse.getBody();
     * logger.debug("obtainedDataProduct: " + obtainedDataProduct);
     * assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
     * assertThat(obtainedDataProduct.getInfo().getDataProductId()).isEqualTo(
     * idDataProductV1);
     * 
     * logger.debug("*** createLifecycle - GET by ID ***");
     * ResponseEntity<DataProductVersionResource> getByIdResponse =
     * rest.getForEntity(
     * apiUrlOfItem(RoutesV1.DATA_PRODUCTS), DataProductVersionResource.class,
     * idDataProductV1);
     * obtainedDataProduct = getByIdResponse.getBody();
     * assertThat(getByIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
     * assertThat(obtainedDataProduct.getInfo().getDataProductId()).isEqualTo(
     * idDataProductV1);
     * 
     * // TODO add checks for other components
     * 
     * InterfaceComponentsResource obtainedInterfaceComponents = obtainedDataProduct
     * .getInterfaceComponents();
     * // inputports check
     * List<PortResource> obtainedInputPorts =
     * obtainedInterfaceComponents.getInputPorts();
     * for (int i = 0; i < obtainedInputPorts.size(); i++) {
     * PortResource p = obtainedInputPorts.get(i);
     * assertThat(p.getId())
     * .isEqualTo(UUID.nameUUIDFromBytes(
     * fqnDataProductV1InputPort.get(i).getBytes())
     * .toString());
     * assertThat(p.getFullyQualifiedName()).isEqualTo(fqnDataProductV1InputPort.get
     * (i));
     * }
     * // outputports check
     * List<PortResource> obtainedOutputPorts =
     * obtainedInterfaceComponents.getOutputPorts();
     * for (int i = 0; i < obtainedOutputPorts.size(); i++) {
     * PortResource p = obtainedOutputPorts.get(i);
     * assertThat(p.getId())
     * .isEqualTo(UUID.nameUUIDFromBytes(
     * fqnDataProductV1OutputPort.get(i).getBytes())
     * .toString());
     * assertThat(p.getFullyQualifiedName()).isEqualTo(fqnDataProductV1OutputPort.
     * get(i));
     * }
     * logger.debug("*** createLifecycle - DELETE ***");
     * mockServer.reset();
     * mockMetaServiceDELETE(idDataProductV1);
     * rest.delete(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL) + idDataProductV1);
     * 
     * logger.debug("*** createLifecycle - GET after DELETE ***");
     * ResponseEntity<Error[]> responseEntity =
     * rest.getForEntity(apiUrlOfItem(RoutesV1.DATA_PRODUCTS),
     * Error[].class, idDataProductV1);
     * List<Error> errorList = Arrays.asList(responseEntity.getBody());
     * assertThat(errorList.size()).isEqualTo(1);
     * assertThat(errorList.get(0).getErrorType()).isEqualTo(
     * "DataProductNotFoundException");
     * } finally {
     * mockServer.reset();
     * mockMetaServiceDELETE(idDataProduct1v1);
     * rest.delete(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL) + idDataProductV1);
     * }
     * }
     * 
     * @Ignore
     * 
     * @Test
     * public void registerDataProductWithComponents() throws IOException,
     * JSONException {
     * try {
     * logger.debug("*** registerDataProductWithComponents ***");
     * mockPolicyServicePOST(true);
     * mockMetaServicePOST();
     * String dataproductJson = readFile(RESOURCE_DP_DPD_COMPONENTS);
     * 
     * ResponseEntity<DataProductVersionResource> responseEntity = rest
     * .postForEntity(apiUrl(RoutesV1.DATA_PRODUCTS), dataproductJson,
     * DataProductVersionResource.class);
     * DataProductVersionResource dataProduct = responseEntity.getBody();
     * 
     * assertThat(dataProduct.getInterfaceComponents().getInputPorts().size()).
     * isEqualTo(2);
     * assertThat(dataProduct.getInterfaceComponents().getInputPorts().get(0).
     * getName())
     * .isEqualTo("tmsTripCDC");
     * 
     * assertThat(dataProduct.getInterfaceComponents().getOutputPorts().size()).
     * isEqualTo(1);
     * assertThat(dataProduct.getInterfaceComponents().getOutputPorts().get(0).
     * getName())
     * .isEqualTo("output-port1");
     * 
     * // checking if all components have been saved in the database
     * responseEntity = rest.getForEntity(apiUrlOfItem(RoutesV1.DATA_PRODUCTS),
     * DataProductVersionResource.class,
     * idDataProductV1);
     * dataProduct = responseEntity.getBody();
     * assertThat(dataProduct.getInterfaceComponents().getInputPorts().size()).
     * isEqualTo(2);
     * assertThat(dataProduct.getInterfaceComponents().getOutputPorts().size()).
     * isEqualTo(1);
     * 
     * rest.delete(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL) + "/" + idDataProductV1);
     * 
     * // testing wrong reference error message
     * dataproductJson = readFile(RESOURCE_DP_DPD_COMPONENTS_WRONGREF);
     * ResponseEntity<Error[]> error =
     * rest.postForEntity(apiUrl(RoutesV1.DATA_PRODUCTS_UPLOADS),
     * dataproductJson,
     * Error[].class);
     * Error[] errors = error.getBody();
     * assertThat(errors.length).isEqualTo(1);
     * assertThat(errors[0].getErrorType()).isEqualTo("ComponentReferenceNotFound");
     * 
     * rest.delete(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL) + "/" + idDataProductV1);
     * 
     * // testing error message for a dataproduct which has two components with same
     * // FQN
     * dataproductJson = readFile(RESOURCE_DP_DPD_COMPONENTS_WRONGREF2);
     * error = rest.postForEntity(apiUrl(RoutesV1.DATA_PRODUCTS_UPLOADS),
     * dataproductJson,
     * Error[].class);
     * errors = error.getBody();
     * assertThat(errors.length).isEqualTo(1);
     * assertThat(errors[0].getErrorType()).isEqualTo("ComponentReferenceNotFound");
     * 
     * } finally {
     * mockServer.reset();
     * mockMetaServiceDELETE(idDataProductV1);
     * rest.delete(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL) + "/" + idDataProductV1);
     * }
     * }
     * 
     * 
     * // creates a data product, gets the list of all data products and verifies
     * that
     * // the data product is inside it.
     * // Then it creates a second data product and requests again the list. After
     * // verifying that both data products are inside the list,
     * // they are deleted
     * 
     * @Ignore
     * 
     * @Test
     * public void getListOfDataProducts() throws IOException {
     * logger.debug("*** getListOfDataProducts - POST ***");
     * mockPolicyServicePOST(true);
     * mockMetaServicePOST();
     * try {
     * String dataproductJson = readFile(RESOURCE_DP_DPD_BASIC);
     * rest.postForEntity(apiUrl(RoutesV1.DATA_PRODUCTS_UPLOADS), dataproductJson,
     * DataProductVersionResource.class);
     * 
     * // get the data product list
     * logger.debug("*** getListOfDataProducts - GET ***");
     * ResponseEntity<DataProductVersionResource[]> getResponse = rest
     * .getForEntity(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL),
     * DataProductVersionResource[].class);
     * assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
     * assertThat(getResponse.getBody().length).isEqualTo(1);
     * 
     * logger.debug("*** getListOfDataProducts - POST ***");
     * mockServer.reset();
     * mockPolicyServicePOST(true);
     * mockMetaServicePOST();
     * String dataproduct2Json = readFile(RESOURCE_DP_DPD_BASIC2);
     * 
     * rest.postForEntity(apiUrl(RoutesV1.DATA_PRODUCTS), dataproduct2Json,
     * DataProductVersionResource.class);
     * 
     * logger.debug("*** getListOfDataProducts - GET ***");
     * getResponse = rest.getForEntity(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL),
     * DataProductVersionResource[].class);
     * assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
     * assertThat(getResponse.getBody().length).isEqualTo(2);
     * 
     * List<DataProductVersionResource> dataProductList;
     * dataProductList = Arrays.asList(getResponse.getBody());
     * assertThat(dataProductList.get(0).getInfo().getDataProductId()).isEqualTo(
     * idDataProductV1);
     * assertThat(dataProductList.get(1).getInfo().getDataProductId()).isEqualTo(
     * idDataProductV2);
     * 
     * // Test for domain and owner id filtering
     * String filterParams = "?ownerId=john.doe@company-xyz.com";
     * getResponse = rest.getForEntity(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL) +
     * filterParams,
     * DataProductVersionResource[].class);
     * dataProductList = Arrays.asList(getResponse.getBody());
     * assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
     * assertThat(dataProductList.get(0).getInfo().getDataProductId()).isEqualTo(
     * idDataProductV1);
     * assertThat(dataProductList.get(1).getInfo().getDataProductId()).isEqualTo(
     * idDataProductV2);
     * } finally {
     * logger.debug("*** getListOfDataProducts - DELETE ***");
     * mockServer.reset();
     * mockMetaServiceDELETE(idDataProductV1);
     * rest.delete(apiUrl(RoutesV1.DATA_PRODUCTS_LISTURL) + "/" + idDataProductV1);
     * logger.debug("*** getListOfDataProducts - DELETE ***");
     * mockServer.reset();
     * mockMetaServiceDELETE(idDataProductV2);
     * rest.delete(apiUrl(RoutesV1.DATA_PRODUCTS) + "/" + idDataProductV2);
     * }
     * }
     * 
     * public void mockPolicyServicePOST(Boolean allow) throws
     * JsonProcessingException {
     * logger.debug("*** mockPolicyResponse : " + allow + " ***");
     * List<ValidatedPolicy> validatedPolicyList = new ArrayList<>();
     * ValidatedPolicy validatedPolicy = new ValidatedPolicy();
     * ValidationResult validationResult = new ValidationResult();
     * Result result = new Result(allow);
     * validationResult.setResult(result);
     * validationResult.setDecisionId("123");
     * validatedPolicy.setPolicy(PolicyName.dataproduct);
     * validatedPolicy.setValidationResult(validationResult);
     * validatedPolicyList.add(validatedPolicy);
     * PolicyValidationResponse policyValidationResponse = new
     * PolicyValidationResponse();
     * policyValidationResponse.setValidatedPolicyList(validatedPolicyList);
     * logger.debug("*** mockPolicyResponse '" + allow + "' ***");
     * mockServer
     * .expect(ExpectedCount.once(),
     * requestTo(policyserviceaddress
     * + "/api/v1/planes/utility/policy-services/opa/validate?id=dataproduct"))
     * .andExpect(content().contentType(MediaType.APPLICATION_JSON))
     * .andExpect(method(HttpMethod.POST))
     * .andRespond(
     * withSuccess(mapper.writeValueAsString(policyValidationResponse),
     * MediaType.APPLICATION_JSON));
     * }
     * 
     * public void mockMetaServicePOST() throws JsonProcessingException {
     * logger.debug("*** mockMetaServicePOST ***");
     * Load load = new Load();
     * load.setDataproductId(idDataProduct1v1);
     * load.setMetaServiceId("123");
     * load.setStatus(LoadStatus.DONE);
     * mockServer
     * .expect(ExpectedCount.once(),
     * requestTo(metaserviceaddress
     * + "/api/v1/planes/utility/meta-services/loads"))
     * .andExpect(content().contentType(MediaType.APPLICATION_JSON))
     * .andExpect(method(HttpMethod.POST))
     * .andRespond(withSuccess(mapper.writeValueAsString(load),
     * MediaType.APPLICATION_JSON));
     * }
     * 
     * public void mockMetaServicePUT() throws JsonProcessingException {
     * logger.debug("*** mockMetaServicePUT ***");
     * Load load = new Load();
     * load.setDataproductId(idDataProduct1v1);
     * load.setMetaServiceId("123");
     * load.setStatus(LoadStatus.DONE);
     * mockServer
     * .expect(ExpectedCount.once(),
     * requestTo(metaserviceaddress
     * + "/api/v1/planes/utility/meta-services/loads"))
     * .andExpect(content().contentType(MediaType.APPLICATION_JSON))
     * .andExpect(method(HttpMethod.PUT))
     * .andRespond(withSuccess(mapper.writeValueAsString(load),
     * MediaType.APPLICATION_JSON));
     * }
     * 
     * public void mockMetaServiceDELETE(String dataProductId) {
     * logger.debug("*** mockMetaServiceDELETE ***");
     * mockServer
     * .expect(ExpectedCount.once(),
     * requestTo(metaserviceaddress
     * + "/api/v1/planes/utility/meta-services/loads?dataProductId="
     * + dataProductId))
     * .andExpect(method(HttpMethod.DELETE))
     * .andRespond(withSuccess());
     * }
     */
}