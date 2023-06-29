package org.opendatamesh.platform.pp.registry.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.notification.EventResource;
import org.opendatamesh.notification.EventType;
import org.opendatamesh.platform.core.dpds.DPDSParser;
import org.opendatamesh.platform.core.dpds.DataProductVersionSource;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.exceptions.BuildException;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.core.dpds.exceptions.ValidationException;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProduct;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.database.repositories.DataProductRepository;
import org.opendatamesh.platform.pp.registry.exceptions.BadGatewayException;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.registry.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.observers.EventNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;

import java.net.URI;
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
    
    // TODO call policy service when a data product is modified
    @Autowired
    private PolicyServiceProxy policyServiceProxy;

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
                    "Impossible to upload data product to metaService", t);
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
     * @param descriptorContent
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
        String descriptorContent, 
        boolean createDataProductIfNotExists,
        String serverUrl // TODO remove form here !!!
    ) {
        DataProductVersion dataProductVersion = null;
        dataProductVersion = descriptorToDataProductVersion(descriptorContent, serverUrl);
        return addDataProductVersion(dataProductVersion.getDataProductId(), dataProductVersion, createDataProductIfNotExists);
    }

     /**
     * 
     * @param descriptorContent
     * @param createDataProductIfNotExists
     * @param serverUrl
     * @return
     * 
     * @throws NotFoundException 
     *      SC404_01_PRODUCT_NOT_FOUND
     * @throws UnprocessableEntityException 
     *      SC422_01_DESCRIPTOR_URI_IS_INVALID
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
        URI descriptorUri, 
        boolean createDataProductIfNotExists,
        String serverUrl // TODO remove form here !!!
    ) {
        
        DataProductVersion dataProductVersion = null;
        dataProductVersion = descriptorToDataProductVersion(descriptorUri, serverUrl);
        return addDataProductVersion(dataProductVersion.getDataProductId(), dataProductVersion, createDataProductIfNotExists);
    }


    // TODO execute compatibility check (version check and API check for the moment)
    //TODO check schemas evolution rules
    private DataProductVersion addDataProductVersion (
        String dataProductId, DataProductVersion dataProductVersion, 
        boolean createDataProductIfNotExists) 
    {
        if(!StringUtils.hasText(dataProductId)) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product id cannot be null");
        }

        if(dataProductVersion == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product version object cannot be null");
        }

        DataProduct dataProduct = dataProductVersion.getDataProduct();
        if(!dataProductId.equals(dataProduct.getId())) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_03_DESCRIPTOR_DOC_SEMANTIC_IS_INVALID,
                "Data product id does not match with the id contained in data product descriptor");
        }

        if(dataProductVersionService.isCompliantWithGlobalPolicies(dataProductVersion)) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_03_DESCRIPTOR_DOC_SEMANTIC_IS_INVALID,
                "Data product descriptor is not compliant with global policies");
        }
        

        if(dataProductExists(dataProductId)) {
            dataProduct = loadDataProduct(dataProduct.getId());
        } else {
            if(createDataProductIfNotExists) {
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

    private DataProductVersion descriptorToDataProductVersion(String descriptorContent, String serverUrl) {
       
        DataProductVersionSource descriptorSource = new DataProductVersionSource(descriptorContent);
        return descriptorToDataProductVersion(descriptorSource, serverUrl);
    }

    private DataProductVersion descriptorToDataProductVersion(URI descriptorUri, String serverUrl) {
        DataProductVersionSource descriptorSource = new DataProductVersionSource(descriptorUri);
        return descriptorToDataProductVersion(descriptorSource, serverUrl);        
    }

    private DataProductVersion descriptorToDataProductVersion(DataProductVersionSource descriptorSource, String serverUrl) {
        DataProductVersion dataProductVersion = null;

        DPDSParser descriptorParser = 
            new DPDSParser(descriptorSource, serverUrl);
       
        DataProductVersionDPDS descriptor = null;
        try {
            descriptor = descriptorParser.parse(true);
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
            "An error occured in the backend descriptor processor while adding read only properties");
            case RESOLVE_STANDARD_DEFINITIONS:
                throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
            "An error occured in the backend descriptor processor while resolving standard definitions");
            default:
              throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                    "An error occured in the backend descriptor processor while adding read only properties");
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
                "Descriptor document does not comply with DPDS. The following validation errors has been found during validation [" + ((ValidationException)e.getCause()).getErrors().toString() + "]");    
        } else {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while loading root document");
        }  
    }

    private void handleResolveExternalResourceException(BuildException e) {

        if(e.getCause() instanceof UnresolvableReferenceException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document contains unresolvable external references: " + e.getMessage());
        } else if(e.getCause() instanceof ParseException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document referentiates external resources that are not valid JSON documents", e);
        } else if(e.getCause() instanceof ValidationException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document does not comply with DPDS. The following validation errors has been found during validation of external references [" + ((ValidationException)e.getCause()).getErrors().toString() + "]");
        } else {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while resolving external references");
        }  
    }

    private void handleResolveInternalResourceException(BuildException e) {

        if(e.getCause() instanceof UnresolvableReferenceException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document contains unresolvable internal references");
        } else if(e.getCause() instanceof ParseException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document that referentiates internal resources that are not valid JSON documents", e);
        } else if(e.getCause() instanceof ValidationException) {
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID,
                "Descriptor document does not comply with DPDS. The following validation errors has been found during internal reference validation [" + ((ValidationException)e.getCause()).getErrors().toString() + "]");
        } else {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                "An unexpected exception occured while resolving internal references");
        }  
    }

   
}
