package org.opendatamesh.platform.pp.registry.services;

import org.opendatamesh.platform.pp.registry.exceptions.BadGatewayException;
import org.opendatamesh.platform.pp.registry.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.registry.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.exceptions.UnprocessableEntityException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.notification.EventResource;
import org.opendatamesh.notification.EventType;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.*;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Template;
import org.opendatamesh.platform.pp.registry.database.repositories.DataProductVersionRepository;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.observers.EventNotifier;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.PolicyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class DataProductVersionService {
    
    @Autowired
    private DataProductVersionRepository dataProductVersionRepository;

    @Autowired
    private  DefinitionService definitionService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private DataProductMapper dataProductMapper;

    @Autowired
    EventNotifier eventNotifier;

    @Autowired
    private PolicyServiceProxy policyServiceProxy;

    private static final Logger logger = LoggerFactory.getLogger(DataProductVersionService.class);

    public DataProductVersionService() { }

    // ======================================================================================
    // CREATE
    // ======================================================================================
    
    protected DataProductVersion createDataProductVersion(DataProductVersion dataProductVersion) {
        return createDataProductVersion(dataProductVersion, true);
    }
    protected DataProductVersion createDataProductVersion(DataProductVersion dataProductVersion, boolean checkGlobalPolicies) {
        if(dataProductVersion == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product version object cannot be null");
        }

        if(dataProductVersionExists(dataProductVersion)) {
            dataProductVersion.getInfo().getVersionNumber();
            dataProductVersion.getInfo().getFullyQualifiedName();
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_05_VERSION_ALREADY_EXISTS,
                "Version [" + dataProductVersion.getInfo().getVersionNumber() + "] of data product [" + dataProductVersion.getInfo().getFullyQualifiedName() + "] already exists");
        }

        //TODO check schemas evolution rules
        if(checkGlobalPolicies && !isCompliantWithGlobalPolicies(dataProductVersion)){
            throw new UnprocessableEntityException(
                OpenDataMeshAPIStandardError.SC422_03_DESCRIPTOR_DOC_SEMANTIC_IS_INVALID,
                "The data product descriptor is not compliant to one or more global policies");
        }
        
        try {
            saveApiDefinitions(dataProductVersion);
        } catch (Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "An internal processing error occured while saving API", t);
        }

        try {
            saveTemplates(dataProductVersion);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "An internal processing error occured while saving templates", t);
        }

        try {
            dataProductVersion = saveDataProductVersion(dataProductVersion);
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while saving version [" + dataProductVersion.getInfo().getVersionNumber() + "] of data product [" + dataProductVersion.getDataProduct().getId() + "]",
                t);
        }

        try {
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_VERSION_CREATED,
                    dataProductVersion.getDataProductId(),
                    null,
                    dataProductMapper.toResource(dataProductVersion).toEventString()
            );
            eventNotifier.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "Impossible to upload data product version to metaService", t);
        }
       
        return dataProductVersion;
    }

    public DataProductVersion saveDataProductVersion(DataProductVersion dataProductVersion) {
        return dataProductVersionRepository.saveAndFlush(dataProductVersion);
    }

    private void saveApiDefinitions(DataProductVersion dataProductVersion) throws JsonMappingException, JsonProcessingException {
        if( dataProductVersion!= null && dataProductVersion.getInterfaceComponents()!= null ) {            
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getInputPorts());
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getOutputPorts());
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getDiscoveryPorts());
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getObservabilityPorts());
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getControlPorts());
        }
    }

    private void saveApiDefinitions(List<Port> ports) throws JsonMappingException, JsonProcessingException {
        if(ports == null || ports.size() == 0) return;

        for(Port port: ports) {
            saveApiDefinition(port);
        }
    }

    private Definition saveApiDefinition(Port port) throws JsonMappingException, JsonProcessingException {
        
        if(port.getPromises() == null) return null;
        if(port.getPromises().getApi() == null) return null;
        
        StandardDefinition api = port.getPromises().getApi();
        Definition apiDefinition = null;
            
        // Api is created first to obtain the id used after for replacing api definition content with a reference url
        try {
            if(StringUtils.hasText(api.getName()) 
                && StringUtils.hasText(api.getVersion())) {
                    apiDefinition = definitionService.searchDefinition(api.getName(), api.getVersion());
            }
            if(apiDefinition == null) {
                apiDefinition = definitionService.createDefinition(new Definition("API", api));
            }
            
        } catch(Throwable t) {
            throw new InternalServerException(
            OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
            "An error occured in the backend database while saving api of port [" +  port.getFullyQualifiedName() + "]",
            t);
        }
            
        // Once we have the api id we replace the definition content with a reference url
        String ref = api.getDefinition().getRef();
        ref = ref.replaceAll("\\{apiId\\}", "" + apiDefinition.getId());
        api.getDefinition().setRef(ref);
             
        ObjectNode portObject = (ObjectNode)objectMapper.readTree(port.getRawContent());
        ObjectNode standardDefinitionContent = (ObjectNode)portObject.at("/promises/api/definition");
        standardDefinitionContent.put("$ref", ref);
        port.setRawContent( objectMapper.writeValueAsString(portObject));     
        
        port.getPromises().setApiId(apiDefinition.getId());
        
        return apiDefinition;
    }

    private void saveTemplates(DataProductVersion dataProductVersion) throws JsonProcessingException {
        if( dataProductVersion!= null && dataProductVersion.getInternalComponents() != null) {
            saveInfrastructuralComponentTemplates(dataProductVersion.getInternalComponents().getInfrastructuralComponents());
            //saveApplicationComponentTemplates(dataProductVersion.getInternalComponents().getApplicationComponents());
        }
    }

    private void saveInfrastructuralComponentTemplates(List<InfrastructuralComponent> components) {
        if(components == null || components.size() == 0) return;

        ExternalResource template = null;

        for(InfrastructuralComponent component: components) {
            template = component.getProvisionInfo().getTemplate();
            saveTemplate(template, component.getId(), component.getFullyQualifiedName());
        }
    }

    private void saveApplicationComponentTemplates(List<ApplicationComponent> components) {
        if(components == null || components.size() == 0) return;

        ExternalResource buildTemplate;
        ExternalResource deployTemplate = null;

        for(ApplicationComponent component : components) {
            buildTemplate = component.getBuildInfo().getTemplate();
            saveTemplate(buildTemplate, component.getId(), component.getFullyQualifiedName());
            deployTemplate = component.getDeployInfo().getTemplate();
            saveTemplate(deployTemplate, component.getId(), component.getFullyQualifiedName());
        }
    }

    private Template saveTemplate(ExternalResource template, String componentId, String componentName) {

        if(template == null) return null;

        Template templateEntity = null;

        try {
            if(StringUtils.hasText(templateEntity.getMediaType())
                    && StringUtils.hasText(templateEntity.getHref())) {
                templateEntity = templateService.searchTemplate(templateEntity.getMediaType(), templateEntity.getHref());
            }
            if(templateEntity == null) {
                templateEntity = templateService.createTemplate(
                        new Template(
                                template.getDescription(),
                                template.getMediaType(),
                                template.getHref()
                        )
                );
            }

        } catch(Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving template of component [" +  componentName + "]",
                    t);
        }

        // Once we have the api id we replace the definition content with a reference url
        /*String ref = api.getDefinition().getRef();
        ref = ref.replaceAll("\\{apiId\\}", "" + apiDefinition.getId());
        api.getDefinition().setRef(ref);

        ObjectNode portObject = (ObjectNode)objectMapper.readTree(port.getRawContent());
        ObjectNode standardDefinitionContent = (ObjectNode)portObject.at("/promises/api/definition");
        standardDefinitionContent.put("$ref", ref);
        port.setRawContent( objectMapper.writeValueAsString(portObject));

        port.getPromises().setApiId(apiDefinition.getId());

        return apiDefinition;*/

        return null;
    }



    // ======================================================================================
    // READ
    // ======================================================================================
    
    // readAllDataProductVersions() 
    // is not implemented beacuse it make 
    // non sense to read al versions across all possible data products.
    // @see searchDataProductVersions(dataProduct)

    private DataProductVersion readDataProductVersion(DataProductVersion dataProductVersion) {
        if(dataProductVersion == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product version object cannot be null");
        }

        if(dataProductVersion.getDataProduct() == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product object cannot be null");
        }
        return readDataProductVersion(dataProductVersion.getDataProduct().getId(), dataProductVersion.getInfo().getVersionNumber());
    }

    public DataProductVersion readDataProductVersion(String dataProductId, String version)  {
        
        DataProductVersion dataProductVersion = null;

        if(!StringUtils.hasText(dataProductId)) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product id cannot be empty");
        }

        try {
            dataProductVersion = getDataProductVersion(dataProductId, version);
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while loading version [" + version + "] of data product [" + dataProductId + "]",
                t);
        }

        if(dataProductVersion == null){
            throw new NotFoundException(
                OpenDataMeshAPIStandardError.SC404_01_PRODUCT_NOT_FOUND,
                "Data product [" + dataProductId + "] does not exist");
        }

        return dataProductVersion;
    }

    private DataProductVersion getDataProductVersion(String dataProductId, String version)  {
        DataProductVersion dataProductVersion = null;

        Optional<DataProductVersion> dataProductVersionLookUpResults = 
            dataProductVersionRepository.findByDataProductIdAndVersionNumber(dataProductId, version);
        if(dataProductVersionLookUpResults.isPresent()){
            dataProductVersion =  dataProductVersionLookUpResults.get();
        } 
        return dataProductVersion;
    }

    // -------------------------
    // exists methods
    // -------------------------
    private boolean dataProductVersionExists(DataProductVersion dataProductVersion) {
        if(dataProductVersion == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product version object cannot be null");
        }

        if(dataProductVersion.getDataProduct() == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product object cannot be null");
        }

        return dataProductVersionExists(
            dataProductVersion.getDataProduct().getId(), 
            dataProductVersion.getInfo().getVersionNumber());
    }

    private boolean dataProductVersionExists(String dataProductId, String version)  {
        return getDataProductVersion(dataProductId, version) != null;
    }

    // -------------------------
    // search methods
    // -------------------------

    public  List<DataProductVersion> searchDataProductVersions(String dataProductId)  {
        List<DataProductVersion> dataProductVersionSearchResult = null;
        
        try {
            dataProductVersionSearchResult = findDataProductVersions(dataProductId);
        } catch(Throwable t) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while searching data product versions",
                t);
        }

        return dataProductVersionSearchResult;
    }

    
    private  List<DataProductVersion> findDataProductVersions(String dataProductId)  {
        return  dataProductVersionRepository.findByDataProductId(dataProductId);
    }


    // ======================================================================================
    // UPDATE
    // ======================================================================================
    
    // Data product versions are immutable objects



    // ======================================================================================
    // DELETE
    // ======================================================================================

    public void deleteDataProductVersion(DataProductVersion dataProductVersion)  {
        if(dataProductVersion == null) {
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                "Data product version object cannot be null");
        }
        deleteDataProductVersion(
            dataProductVersion.getDataProductId(), dataProductVersion.getVersionNumber() );
    }

    public void deleteAllDataProductVersions(String dataProductId){
        List<DataProductVersion> dataProductVersions = searchDataProductVersions(dataProductId);
        
        for (DataProductVersion dataProductVersion: dataProductVersions) {
            deleteDataProductVersion(dataProductVersion);   
        }
    }

    public void deleteDataProductVersion(String dataProductId, String versionNumber)  {
        DataProductVersion dataProductVersion = readDataProductVersion(dataProductId, versionNumber);
        
        try {
            dataProductVersionRepository.delete(dataProductVersion);
            logger.info("Data product version [" + versionNumber + "] of data product [" + dataProductId + "] succesfully deleted");
        } catch(Throwable t){
            throw new InternalServerException(
                OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while deleting data product version",
                t);
        }

        try {
            //metaServiceProxy.uploadDataProductVersion(dataProductVersion);
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_VERSION_DELETED,
                    dataProductVersion.getDataProductId(),
                    dataProductMapper.toResource(dataProductVersion).toEventString(),
                    null
            );
            eventNotifier.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "Impossible to upload data product version to metaService", t);
        }

    }

   
    // ======================================================================================
    // OTHER
    // ======================================================================================
   


    public boolean isCompliantWithGlobalPolicies(DataProductVersion dataProductVersion){
        Boolean isValid = false;
        
        try {
            policyServiceProxy.validateDataProductVersion(
                dataProductVersion, PolicyName.dataproduct);
        } catch (Throwable t) {
            throw new BadGatewayException(
                OpenDataMeshAPIStandardError.SC502_01_POLICY_SERVICE_ERROR,
                "An error occured while invoking policy service to validate data product version ",
                t);
        }
        
        return isValid;
    }

    

}
