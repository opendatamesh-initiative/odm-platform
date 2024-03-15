package org.opendatamesh.platform.pp.registry.server.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.core.dpds.exceptions.*;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.IdentifierStrategy;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.DataProduct;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.database.repositories.DataProductRepository;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class DataProductService {

    @Autowired
    private DataProductVersionService dataProductVersionService;

    @Autowired
    private DataProductRepository dataProductRepository;

    @Autowired
    VariableService variableService;
   
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private DataProductMapper dataProductMapper;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;

    @Autowired
    EventNotifierProxy eventNotifierProxy;

    @Value("${odm.schemas.validation.baseUrl}")
    private String schemaValidationBaseUrl;

    @Value("${odm.schemas.validation.supportedVersions.min}")
    private String schemaValidationMinSupportedVersion;

    @Value("${odm.schemas.validation.supportedVersions.max}")
    private String schemaValidationMaxSupportedVersion;

    private static final Logger logger = LoggerFactory.getLogger(DataProductService.class);

    public DataProductService() { }

    // ======================================================================================
    // CREATE
    // ======================================================================================
    
    public DataProduct createDataProduct(DataProduct dataProduct) {
     if(dataProduct == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                "Data product object cannot be null");
        }

        if(!StringUtils.hasText(dataProduct.getFullyQualifiedName())) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID,
                "Data product fullyQualifiedName property cannot be empty");
        }

        String uuid = IdentifierStrategy.DEFUALT.getId(dataProduct.getFullyQualifiedName());
        if(dataProduct.getId() != null && !dataProduct.getId().equals(uuid)) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID,
                "Data product [" + dataProduct.getFullyQualifiedName() + "] with id [" + dataProduct.getId()+ "] is invalid. Expected [" + uuid + "]");
        }
        dataProduct.setId(uuid);
        
        if(loadDataProduct(uuid) != null) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_04_PRODUCT_ALREADY_EXISTS,
                "Data product [" + dataProduct.getFullyQualifiedName() + "] already exists");
        }
       
        try {
            dataProduct = saveDataProduct(dataProduct);
            logger.info("Data product [" + dataProduct.getFullyQualifiedName() + "] succesfully created");
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while saving data product [" + dataProduct.getFullyQualifiedName() + "]",
                t);
        }

        eventNotifierProxy.notifyDataProductCreation(dataProductMapper.toResource(dataProduct));
       
        return dataProduct;
    }

    private DataProduct saveDataProduct(DataProduct dataProduct) {
        return dataProductRepository.saveAndFlush(dataProduct);
    }

    // ======================================================================================
    // READ
    // ======================================================================================
    
    public List<DataProduct> readAllDataProducts() {
        List<DataProduct> dataProducts = null;
        try {
            dataProducts = loadAllDataProducts();
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while loading data products",
                t);
        }
        return dataProducts;
    }

    private List<DataProduct> loadAllDataProducts() {
        return dataProductRepository.findAll();
    }

    public DataProduct readDataProduct(DataProduct dataProduct)  {
        if(dataProduct == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                "Data product object cannot be null");
        }
        return readDataProduct(dataProduct.getId());
    }

    public DataProduct readDataProduct(String dataProductId)  {

        DataProduct dataProduct = null;
        
        if(!StringUtils.hasText(dataProductId)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Data product id is empty");
        }

        try {
            dataProduct = loadDataProduct(dataProductId);
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while loading data product with id [" + dataProductId + "]",
                t);
        }
       
        if(dataProduct == null){
            throw new NotFoundException(
                RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                "Data Product with id [" + dataProductId + "] does not exist");
        }

        return dataProduct;
    }
    
    private DataProduct loadDataProduct(String dataProductId) {
        DataProduct dataProduct = null;
        
        Optional<DataProduct> dataProductLookUpResults = dataProductRepository.findById(dataProductId);
            
        if(dataProductLookUpResults.isPresent()){
            dataProduct = dataProductLookUpResults.get();
        } 
     
        return dataProduct;
    }

    // -------------------------
    // exists methods
    // -------------------------

    private boolean dataProductExists(DataProduct dataProduct)  {
        if(dataProduct == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                "Data product object cannot be null");
        }
        return dataProductRepository.existsById(dataProduct.getId());
    }

    public boolean dataProductExists(String dataProductId)  {
        return dataProductRepository.existsById(dataProductId);
    }
    
    
    // -------------------------
    // search methods
    // -------------------------
    public List<DataProduct> searchDataProducts(String fqn, String domain) {
        List<DataProduct> dataProductSearchResults = null;
        try {
            dataProductSearchResults = findDataProducts(fqn, domain);
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while searching data products",
                t);
        }
        return dataProductSearchResults;
    }

    private List<DataProduct> findDataProducts(String fqn, String domain) {
        return dataProductRepository
            .findAll(DataProductRepository.Specs.hasMatch(fqn, domain));
    }



    // ======================================================================================
    // UPDATE
    // ======================================================================================
    
    public DataProduct updateDataProduct(DataProduct dataProduct) {

        if(dataProduct == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Data product object cannot be null");
        }

        if(StringUtils.hasText(dataProduct.getId()) && StringUtils.hasText(dataProduct.getFullyQualifiedName())){
            String generatedUuid = IdentifierStrategy.DEFUALT.getId(dataProduct.getFullyQualifiedName());
            if(generatedUuid.equals(dataProduct.getId()) == false) {
                throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID,
                    "Data product id [" + dataProduct.getId() + "] does not match with fullyQualifiedName [" + dataProduct.getFullyQualifiedName() + "]. Expecyed id is [" + generatedUuid + "]");
            }
        }

        String uuid = null;
        if(StringUtils.hasText(dataProduct.getId())) {
            uuid = dataProduct.getId();
        } else if(StringUtils.hasText(dataProduct.getFullyQualifiedName())) {
            uuid = IdentifierStrategy.DEFUALT.getId(dataProduct.getFullyQualifiedName());
        } else {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID,
                    "Data product id and fullyQualifiedName properties cannot be both empty");
        }
        DataProduct oldDataProduct = loadDataProduct(uuid);
       
       
        if(oldDataProduct == null) {
            throw new NotFoundException(
                RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                    "Data product [" + dataProduct.getFullyQualifiedName() + "] with id [" + dataProduct.getId() + "] doesn't exists");
        }
        dataProduct.setId(oldDataProduct.getId());
        dataProduct.setFullyQualifiedName(oldDataProduct.getFullyQualifiedName());

        try {
            dataProduct = saveDataProduct(dataProduct);
            logger.info("Data product [" + dataProduct.getFullyQualifiedName() + "] with id [" + dataProduct.getId() + "] succesfully updated");
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while updating data product [" + dataProduct.getFullyQualifiedName() + "] with id [" + dataProduct.getId() + "]",
                t);
        }

        eventNotifierProxy.notifyDataProductUpdate(
                dataProductMapper.toResource(oldDataProduct),
                dataProductMapper.toResource(dataProduct)
        );

        return dataProduct;
    }

    // ======================================================================================
    // DELETE
    // ======================================================================================
    
    public DataProduct deleteDataProduct(String dataProductId)  {

        DataProduct dataProduct = readDataProduct(dataProductId);
        
        dataProductVersionService.deleteAllDataProductVersions(dataProduct.getId());
        
        try {
            dataProductRepository.delete(dataProduct);
            logger.info("Data product with id [" + dataProductId + "] successfully deleted");
        } catch(Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while deleting data product",
                t);
        }

        eventNotifierProxy.notifyDataProductDeletion(dataProductMapper.toResource(dataProduct));

        return dataProduct;
    }



    // ======================================================================================
    // OTHER...
    // ======================================================================================

    /**
     * 
     * @param descriptorLocation
     * @param createDataProductIfNotExists
     * @param serverUrl
     * @return
     * 
     * @throws NotFoundException 
     *      SC404_01_PRODUCT_NOT_FOUND
     * @throws UnprocessableEntityException 
     *      SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID
     *      SC422_03_DESCRIPTOR_DOC_SEMANTIC_IS_INVALID
     *      SC422_05_VERSION_ALREADY_EXISTS
     * @throws InternalServerException 
     *      SC500_SERVICE_ERROR
     *      SC500_DATABASE_ERROR
     * @throws BadGatewayException 
     *      SC502_01_POLICY_SERVICE_ERROR
     *      SC502_05_META_SERVICE_ERROR
     */
     public DataProductVersion addDataProductVersion(
        String dataProductId,
        DescriptorLocation descriptorLocation, 
        String serverUrl // TODO remove form here !!!
    ) {
        if(!StringUtils.hasText(dataProductId)) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                "Data product id cannot be null");
        }

        DataProduct dataProduct = readDataProduct(dataProductId);
        DataProductVersion dataProductVersion = null;
        dataProductVersion = descriptorToDataProductVersion(descriptorLocation, serverUrl);
        if(!dataProduct.getId().equals(dataProductVersion.getInfo().getDataProductId())) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_03_DESCRIPTOR_NOT_COMPLIANT,
                "Data product fqn [" + dataProduct.getFullyQualifiedName() + "] does not match with the fqn [" + dataProductVersion.getInfo().getFullyQualifiedName() + "] contained in data product descriptor");
        }
        
        return addDataProductVersion(dataProductVersion, false, serverUrl);
    }



    public DataProductVersion addDataProductVersion(
        DescriptorLocation descriptorLocation, 
        boolean createDataProductIfNotExists,
        String serverUrl // TODO remove form here !!!
    ) {

        DataProductVersion dataProductVersion = null;
        dataProductVersion = descriptorToDataProductVersion(descriptorLocation, serverUrl);
        return addDataProductVersion(dataProductVersion, createDataProductIfNotExists, serverUrl);
    }

   
    // TODO execute compatibility check (version check and API check for the moment)
    //TODO check schemas evolution rules
    private DataProductVersion addDataProductVersion (
        DataProductVersion dataProductVersion, 
        boolean createDataProductIfNotExists, String serverUrl) 
    {
        
        if(dataProductVersion == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                "Data product version object cannot be null");
        }

        if(!dataProductVersionService.isCompliantWithGlobalPolicies(dataProductVersion)) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_03_DESCRIPTOR_NOT_COMPLIANT,
                "Data product descriptor is not compliant with global policies");
        }
        
        DataProduct dataProduct = null;
        String dataProductId = dataProductVersion.getInfo().getDataProductId();
        if(dataProductExists(dataProductId)) {
            dataProduct = loadDataProduct(dataProductId);
        } else {
            if(createDataProductIfNotExists) {
                dataProduct = dataProductVersion.getDataProduct();
                dataProduct = createDataProduct(dataProduct);
            } else {
                throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                "Data product [" + dataProductId + "] not found");
            } 
        }

        dataProductVersion = dataProductVersionService.createDataProductVersion(dataProductVersion, false, serverUrl);

        // Search for variables and save them
        try {
            variableService.searchAndSaveVariablesFromDescriptor(
                    objectMapper.writeValueAsString(dataProductVersion),
                    dataProductVersion.getDataProductId(),
                    dataProductVersion.getVersionNumber()
            );
        } catch (JsonProcessingException e) {
            logger.warn("Error serializing Data Product Version as string before searching for variables. ", e);
        } catch (Throwable t) {
            throw t;
        }

        return dataProductVersion;
    }

    private DataProductVersion descriptorToDataProductVersion(DescriptorLocation descriptorLocation, String serverUrl) {
        DataProductVersion dataProductVersion = null;

        DPDSParser descriptorParser = new DPDSParser(
                schemaValidationBaseUrl,
                schemaValidationMinSupportedVersion,
                schemaValidationMaxSupportedVersion
        );
        ParseOptions options = new ParseOptions();
        options.setServerUrl(serverUrl);
               
        DataProductVersionDPDS descriptor = null;
        try {
            ParseResult result = descriptorParser.parse(descriptorLocation, options);
            descriptor = result.getDescriptorDocument();
        } catch (ParseException e) {
            handleBuildException(e);
        }
     
        dataProductVersion = dataProductVersionMapper.toEntity(descriptor);
        dataProductVersion.setDataProductId(dataProductVersion.getInfo().getDataProductId());
        dataProductVersion.setVersionNumber(dataProductVersion.getInfo().getVersionNumber());

        return dataProductVersion;
    }

    private void handleBuildException(ParseException e) {
        switch(e.getStage()) {
            case LOAD_ROOT_DOC:
                handleLoadRootDocException(e);
                break;
            case RESOLVE_EXTERNAL_REFERENCES:
                handleResolveExternalResourceException(e);
                break;
            case RESOLVE_INTERNAL_REFERENCES:
                handleResolveInternalResourceException(e);
                break;
            case RESOLVE_READ_ONLY_PROPERTIES:
                throw new InternalServerException(
                    ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
            "An error occured in the backend descriptor processor while adding read only properties", e);
            case RESOLVE_STANDARD_DEFINITIONS:
                throw new InternalServerException(
                    ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
            "An error occured in the backend descriptor processor while resolving standard definitions", e);
            case VALIDATE:
                throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_02_DESCRIPTOR_NOT_VALID,
                "Descriptor document does not comply with DPDS. The following validation errors has been found during validation [" + ((ValidationException)e.getCause()).getErrors().toString() + "]", e);    
            default:
              throw new InternalServerException(
                ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
                    "An error occured in the backend descriptor processor while adding read only properties", e);
          }
    }

    private void handleLoadRootDocException(ParseException e) {

        if(e.getCause() instanceof FetchException) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_01_DESCRIPTOR_URI_NOT_VALID,
                "Provided URI cannot be fatched [" + ((FetchException)e.getCause()).getUri() + "]", e);
        } else if(e.getCause() instanceof DeserializationException) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_02_DESCRIPTOR_NOT_VALID,
                "Descriptor document is not a valid JSON document", e);    
        }  else {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while loading root document", e);
        }  
    }

    private void handleResolveExternalResourceException(ParseException e) {

        if(e.getCause() instanceof UnresolvableReferenceException) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_02_DESCRIPTOR_NOT_VALID,
                "Descriptor document contains unresolvable external references: " + e.getMessage(), e);
        } else if(e.getCause() instanceof DeserializationException) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_02_DESCRIPTOR_NOT_VALID,
                "Descriptor document referentiates external resources that are not valid JSON documents", e);
        }  else {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while resolving external references", e);
        }  
    }

    private void handleResolveInternalResourceException(ParseException e) {

        if(e.getCause() instanceof UnresolvableReferenceException) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_02_DESCRIPTOR_NOT_VALID,
                "Descriptor document contains unresolvable internal references", e);
        } else if(e.getCause() instanceof DeserializationException) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_02_DESCRIPTOR_NOT_VALID,
                "Descriptor document that referentiates internal resources that are not valid JSON documents", e);
        } else {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while resolving internal references", e);
        }  
    }
   
}
