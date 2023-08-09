package org.opendatamesh.platform.pp.registry.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.exceptions.*;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProduct;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.database.repositories.DataProductRepository;
import org.opendatamesh.platform.pp.registry.exceptions.*;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.observers.EventNotifier;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DataProductService {

    @Autowired
    private DataProductVersionService dataProductVersionService;

    @Autowired
    private DataProductRepository dataProductRepository;
   
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private DataProductMapper dataProductMapper;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;

    @Autowired
    EventNotifier eventNotifier;
    

    private static final Logger logger = LoggerFactory.getLogger(DataProductService.class);

    public DataProductService() { }

    // ======================================================================================
    // CREATE
    // ======================================================================================
    
    /**
     * 
     * @param dataProduct 
     * @return
     * 
     * @throws UnprocessableEntityException 
     *      SC422_DATAPRODUCT_DOC_SEMANTIC_IS_INVALID: fqn empty, invalid id
     *      SC422_DATAPRODUCT_ALREADY_EXISTS
     * @throws InternalServerException 
     *      SC500_SERVICE_ERROR
     *      SC500_DATABASE_ERROR
     */
    public DataProduct createDataProduct(DataProduct dataProduct) {

        if(dataProduct == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product object cannot be null");
        }

        if(!StringUtils.hasText(dataProduct.getFullyQualifiedName())) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_07_PRODUCT_IS_INVALID,
                "Data product fullyQualifiedName property cannot be empty");
        }
        
        if(searchDataProductsByFQN(dataProduct.getFullyQualifiedName()) != null) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_04_PRODUCT_ALREADY_EXISTS,
                "Data product [" + dataProduct.getFullyQualifiedName() + "] already exists");
        }

        String uuid = UUID.nameUUIDFromBytes(dataProduct.getFullyQualifiedName().getBytes()).toString();
        if(dataProduct.getId() != null && !dataProduct.getId().equals(uuid)) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_07_PRODUCT_IS_INVALID,
                "Data product [" + dataProduct.getFullyQualifiedName() + "] id [" + dataProduct.getId()+ "] is invalid. Expected [" + uuid + "]");
        }
        dataProduct.setId(uuid);
       
        try {
            dataProduct = saveDataProduct(dataProduct);
            logger.info("Data product [" + dataProduct.getFullyQualifiedName() + "] succesfully created");
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while saving data product [" + dataProduct.getFullyQualifiedName() + "]",
                t);
        }

        try {
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_CREATED,
                    dataProduct.getId(),
                    null,
                    dataProductMapper.toResource(dataProduct).toEventString()
            );
            eventNotifier.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "Impossible to upload data product to metaService: " + t.getMessage(),
                    t
            );
        }
       
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
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
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
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product object cannot be null");
        }
        return readDataProduct(dataProduct.getId());
    }
    public DataProduct readDataProduct(String dataProductId)  {

        DataProduct dataProduct = null;
        
        if(!StringUtils.hasText(dataProductId)) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Data product id is empty");
        }

        try {
            dataProduct = loadDataProduct(dataProductId);
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while loading data product with id [" + dataProductId + "]",
                t);
        }
       
        if(dataProduct == null){
            throw new NotFoundException(
                OpenDataMeshAPIStandardError.SC404_01_PRODUCT_NOT_FOUND,
                "Data Product with [" + dataProductId + "] does not exist");
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
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
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
    public List<DataProduct> searchDataProductsByDomainAndOwner(String domain, String ownerId) {
        List<DataProduct> dataProductSearchResults = null;
        try {
            dataProductSearchResults = findDataProductsByDomainAndOwner(domain, ownerId);
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while searching data products",
                t);
        }
        return dataProductSearchResults;
    }

    private List<DataProduct> findDataProductsByDomainAndOwner(String domain, String ownerId) {
        return dataProductRepository.findAll(getDataProductsLookUpQuery(domain, ownerId));
    }

    // TODO move this code to repository class
    private Specification<DataProduct> getDataProductsLookUpQuery(String domain, String ownerId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (domain != null)
                predicates.add(criteriaBuilder.equal(root.get("domain"), domain));
            if (ownerId != null)
                predicates.add(criteriaBuilder.equal(root.get("info").get("owner").get("id"), ownerId));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public DataProduct searchDataProductsByFQN(String fullyQualifiedName) {
        DataProduct dataProduct = null;
        List<DataProduct> dataProductSearchResults = null;
        try {
            dataProductSearchResults = findDataProductsByFQN(fullyQualifiedName);
            if(!dataProductSearchResults.isEmpty()) {
                dataProduct = dataProductSearchResults.get(0);
            }
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while searching data products",
                t);
        }
        return dataProduct;
    }

    private List<DataProduct> findDataProductsByFQN(String fullyQualifiedName) {
        return dataProductRepository.findAll(getDataProductsLookUpQuery2(fullyQualifiedName));
    }

    // TODO move this code to repository class
    private Specification<DataProduct> getDataProductsLookUpQuery2(String fullyQualifiedName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (fullyQualifiedName != null)
                predicates.add(criteriaBuilder.equal(root.get("fullyQualifiedName"), fullyQualifiedName));
           return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }



    // ======================================================================================
    // UPDATE
    // ======================================================================================
    
    public DataProduct updateDataProduct(DataProduct dataProduct) {

        if(dataProduct == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Data product object cannot be null");
        }

        if(!StringUtils.hasText(dataProduct.getFullyQualifiedName())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_07_PRODUCT_IS_INVALID,
                    "Data product fullyQualifiedName property cannot be empty");
        }

        DataProduct oldDataProduct = searchDataProductsByFQN(dataProduct.getFullyQualifiedName());
        if(oldDataProduct == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_01_PRODUCT_NOT_FOUND,
                    "Data product [" + dataProduct.getFullyQualifiedName() + "] doesn't exists");
        }
        dataProduct.setId(oldDataProduct.getId());

        try {
            dataProduct = saveDataProduct(dataProduct);
            logger.info("Data product [" + dataProduct.getFullyQualifiedName() + "] succesfully updated");
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while updating data product [" + dataProduct.getFullyQualifiedName() + "]",
                t);
        }

        try {
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_UPDATED,
                    dataProduct.getId(),
                    dataProductMapper.toResource(oldDataProduct).toEventString(),
                    dataProductMapper.toResource(dataProduct).toEventString()
            );
            eventNotifier.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "Impossible to upload data product version to metaService", t);
        }

        return dataProduct;
    }

    // ======================================================================================
    // DELETE
    // ======================================================================================
    
    public void deleteDataProduct(String dataProductId)  {
        DataProduct dataProduct = readDataProduct(dataProductId);
        dataProductVersionService.deleteAllDataProductVersions(dataProduct.getId());
        try {
            dataProductRepository.delete(dataProduct);
            logger.info("Data product with id [" + dataProductId + "] successfully deleted");
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while deleting data product",
                t);
        }

        try {
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_DELETED,
                    dataProduct.getId(),
                    dataProductMapper.toResource(dataProduct).toEventString(),
                    null
            );
            eventNotifier.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "Impossible to upload data product to metaService", t);
        }

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
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product id cannot be null");
        }

        DataProduct dataProduct = readDataProduct(dataProductId);
        DataProductVersion dataProductVersion = null;
        dataProductVersion = descriptorToDataProductVersion(descriptorLocation, serverUrl);
        if(!dataProduct.getId().equals(dataProductVersion.getInfo().getDataProductId())) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_03_DESCRIPTOR_DOC_SEMANTIC_IS_INVALID,
                "Data product id [" + dataProduct.getFullyQualifiedName() + "]does not match with the id [" + dataProductVersion.getInfo().getFullyQualifiedName() + "] contained in data product descriptor");
        }
        
        
        return addDataProductVersion(dataProductVersion, false);
    }


    public DataProductVersion addDataProductVersion(
        DescriptorLocation descriptorLocation, 
        boolean createDataProductIfNotExists,
        String serverUrl // TODO remove form here !!!
    ) {

        DataProductVersion dataProductVersion = null;
        dataProductVersion = descriptorToDataProductVersion(descriptorLocation, serverUrl);
        return addDataProductVersion(dataProductVersion, createDataProductIfNotExists);
    }

   
    // TODO execute compatibility check (version check and API check for the moment)
    //TODO check schemas evolution rules
    private DataProductVersion addDataProductVersion (
        DataProductVersion dataProductVersion, 
        boolean createDataProductIfNotExists) 
    {
        
        if(dataProductVersion == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product version object cannot be null");
        }

        if(dataProductVersionService.isCompliantWithGlobalPolicies(dataProductVersion)) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_03_DESCRIPTOR_DOC_SEMANTIC_IS_INVALID,
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
                    OpenDataMeshAPIStandardError.SC404_01_PRODUCT_NOT_FOUND,
                "Data product [" + dataProductId + "] not found");
            } 
        }

        dataProductVersion = dataProductVersionService.createDataProductVersion(dataProductVersion, false);

        return dataProductVersion;
    }

    private DataProductVersion descriptorToDataProductVersion(DescriptorLocation descriptorLocation, String serverUrl) {
        DataProductVersion dataProductVersion = null;

        DPDSParser descriptorParser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl(serverUrl);
               
        DataProductVersionDPDS descriptor = null;
        try {
            ParseResult result = descriptorParser.parse(descriptorLocation, options);
            descriptor = result.getDescriptorDocument();
        } catch (BuildException e) {
            handleBuildException(e);
        }
     
        dataProductVersion = dataProductVersionMapper.toEntity(descriptor);
        dataProductVersion.setDataProductId(dataProductVersion.getInfo().getDataProductId());
        dataProductVersion.setVersionNumber(dataProductVersion.getInfo().getVersionNumber());

        return dataProductVersion;
    }

    private void handleBuildException(BuildException e) {
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
                    OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
            "An error occured in the backend descriptor processor while adding read only properties", e);
            case RESOLVE_STANDARD_DEFINITIONS:
                throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
            "An error occured in the backend descriptor processor while resolving standard definitions", e);
            default:
              throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                    "An error occured in the backend descriptor processor while adding read only properties", e);
          }
    }

    private void handleLoadRootDocException(BuildException e) {

        if(e.getCause() instanceof FetchException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_01_DESCRIPTOR_URI_IS_INVALID,
                "Provided URI cannot be fatched [" + ((FetchException)e.getCause()).getUri() + "]", e);
        } else if(e.getCause() instanceof ParseException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document it's not a valid JSON document", e);    
        } else if(e.getCause() instanceof ValidationException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document does not comply with DPDS. The following validation errors has been found during validation [" + ((ValidationException)e.getCause()).getErrors().toString() + "]", e);    
        } else {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while loading root document", e);
        }  
    }

    private void handleResolveExternalResourceException(BuildException e) {

        if(e.getCause() instanceof UnresolvableReferenceException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document contains unresolvable external references: " + e.getMessage(), e);
        } else if(e.getCause() instanceof ParseException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document referentiates external resources that are not valid JSON documents", e);
        } else if(e.getCause() instanceof ValidationException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document does not comply with DPDS. The following validation errors has been found during validation of external references [" + ((ValidationException)e.getCause()).getErrors().toString() + "]", e);
        } else {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while resolving external references", e);
        }  
    }

    private void handleResolveInternalResourceException(BuildException e) {

        if(e.getCause() instanceof UnresolvableReferenceException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document contains unresolvable internal references", e);
        } else if(e.getCause() instanceof ParseException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document that referentiates internal resources that are not valid JSON documents", e);
        } else if(e.getCause() instanceof ValidationException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document does not comply with DPDS. The following validation errors has been found during internal reference validation [" + ((ValidationException)e.getCause()).getErrors().toString() + "]", e);
        } else {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while resolving internal references", e);
        }  
    }

   
}
