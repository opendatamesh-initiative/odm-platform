package org.opendatamesh.platform.pp.registry.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadGatewayException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.Api;
import org.opendatamesh.platform.pp.registry.server.database.entities.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.server.database.entities.Schema;
import org.opendatamesh.platform.pp.registry.server.database.entities.Template;
import org.opendatamesh.platform.pp.registry.server.database.entities.ApiToSchemaRelationship.ApiToSchemaRelationshipId;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.*;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.Component;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.StandardDefinition;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.TemplateStandardDefinition;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.definitions.ApiDefinitionEndpoint;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.definitions.ApiDefinitionReference;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces.Port;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.ApplicationComponent;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.InfrastructuralComponent;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.LifecycleActivityInfo;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals.LifecycleInfo;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.database.repositories.DataProductVersionRepository;
import org.opendatamesh.platform.pp.registry.server.resources.v1.observers.EventNotifier;
import org.opendatamesh.platform.pp.registry.server.resources.v1.policyservice.PolicyName;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.resources.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DataProductVersionService {

    @Autowired
    private DataProductVersionRepository dataProductVersionRepository;

    @Autowired
    private ApiService apiService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private SchemaService schemaService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;

    @Autowired
    EventNotifier eventNotifier;

    @Autowired
    private PolicyServiceProxy policyServiceProxy;

    private static final Logger logger = LoggerFactory.getLogger(DataProductVersionService.class);

    public DataProductVersionService() {
    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    protected DataProductVersion createDataProductVersion(DataProductVersion dataProductVersion, String serverUrl) {
        return createDataProductVersion(dataProductVersion, true,serverUrl);
    }

    protected DataProductVersion createDataProductVersion(
        DataProductVersion dataProductVersion,
            boolean checkGlobalPolicies, String serverUrl) {
        if (dataProductVersion == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Data product version object cannot be null");
        }

        if (dataProductVersionExists(dataProductVersion)) {
            dataProductVersion.getInfo().getVersionNumber();
            dataProductVersion.getInfo().getFullyQualifiedName();
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_06_VERSION_ALREADY_EXISTS,
                    "Version [" + dataProductVersion.getInfo().getVersionNumber() + "] of data product ["
                            + dataProductVersion.getInfo().getFullyQualifiedName() + "] already exists");
        }

        // TODO check schemas evolution rules
        if (checkGlobalPolicies && !isCompliantWithGlobalPolicies(dataProductVersion)) {
            throw new UnprocessableEntityException(
                RegistryApiStandardErrors.SC422_03_DESCRIPTOR_NOT_COMPLIANT,
                    "The data product descriptor is not compliant to one or more global policies");
        }

        try {
            saveApis(dataProductVersion, serverUrl);
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "An internal processing error occured while saving API", t);
        }

        try {
            saveTemplates(dataProductVersion, serverUrl);
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "An internal processing error occured while saving templates", t);
        }

        try {
            dataProductVersion = saveDataProductVersion(dataProductVersion);
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving version ["
                            + dataProductVersion.getInfo().getVersionNumber() + "] of data product ["
                            + dataProductVersion.getDataProduct().getId() + "]",
                    t);
        }

        try {
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_VERSION_CREATED,
                    dataProductVersion.getDataProductId(),
                    null,
                    dataProductVersionMapper.toResource(dataProductVersion).toEventString());
            eventNotifier.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    "Impossible to upload data product version to metaService: " + t.getMessage()
                    , t
            );
        }

        return dataProductVersion;
    }

    public DataProductVersion saveDataProductVersion(DataProductVersion dataProductVersion) {
        return dataProductVersionRepository.saveAndFlush(dataProductVersion);
    }

    private void saveApis(DataProductVersion dataProductVersion, String serverUrl)
            throws JsonProcessingException {
        if (dataProductVersion != null && dataProductVersion.getInterfaceComponents() != null) {
            saveApis(dataProductVersion.getInterfaceComponents().getInputPorts(), serverUrl);
            saveApis(dataProductVersion.getInterfaceComponents().getOutputPorts(), serverUrl);
            saveApis(dataProductVersion.getInterfaceComponents().getDiscoveryPorts(), serverUrl);
            saveApis(dataProductVersion.getInterfaceComponents().getObservabilityPorts(), serverUrl);
            saveApis(dataProductVersion.getInterfaceComponents().getControlPorts(), serverUrl);
        }
    }

    private void saveApis(List<Port> ports, String serverUrl) throws JsonProcessingException {
        if (ports == null || ports.size() == 0)
            return;

        for (Port port : ports) {
            Map<ApiDefinitionEndpoint, Schema> schemas = saveApiSchemas(port);
            Api apiDefinition = saveApi(port, serverUrl);
            saveApiToSchemaRelationship(apiDefinition, schemas);
        }
    }

    private void saveApiToSchemaRelationship(Api apiDefinition, Map<ApiDefinitionEndpoint, Schema> schemas) {
        for (Map.Entry<ApiDefinitionEndpoint, Schema> entry : schemas.entrySet()) {
            ApiToSchemaRelationship relationship = new ApiToSchemaRelationship();
            relationship.setId(new ApiToSchemaRelationshipId(apiDefinition.getId(), entry.getValue().getId()));

            relationship.setOperationId(entry.getKey().getName());
            relationship.setOutputMediaType(entry.getKey().getOutputMediaType());
            if (schemaService.searchRelationship(relationship.getId()) == null) {
                schemaService.createApiToSchemaRelationship(relationship);
            } else {
                schemaService.updateRelationship(relationship);
            }
        }
    }

    private Map<ApiDefinitionEndpoint, Schema> saveApiSchemas(Port port) {
        Map<ApiDefinitionEndpoint, Schema> schemas = new HashMap<ApiDefinitionEndpoint, Schema>();
        if (port.getPromises() == null)
            return schemas;
        if (port.getPromises().getApi() == null)
            return schemas;

        if (port.getPromises().getApi().getDefinition() instanceof ApiDefinitionReference) {
            ApiDefinitionReference apiDefinitionReference = (ApiDefinitionReference) port.getPromises().getApi()
                    .getDefinition();
            List<ApiDefinitionEndpoint> endpoints = apiDefinitionReference.getEndpoints();

            for (ApiDefinitionEndpoint endpoint : endpoints) {
                Schema schema = saveApiSchema(endpoint);
                schemas.put(endpoint, schema);
            }
        } else {
            System.out.println("Ops: " + port.getPromises().getApi().getDefinition().getClass().getName());
        }

        return schemas;
    }

    private Schema saveApiSchema(ApiDefinitionEndpoint endpoint) {
        Schema schema = null;

        Schema newSchema = new Schema(); // why not a mapper?
        newSchema.setName(endpoint.getSchema().getName());
        newSchema.setVersion(endpoint.getSchema().getVersion());
        newSchema.setMediaType(endpoint.getSchema().getMediaType());
        newSchema.setContent(endpoint.getSchema().getContent());

        try {
            schema = schemaService.searchSchema(newSchema);
            if (schema == null) {
                schema = schemaService.createSchema(newSchema);
            }
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while schema of endpoint [" + endpoint
                            + "]",
                    t);
        }

        // rewrite schema ref withing api def

        return schema;
    }

    private Api saveApi(Port port, String serverUrl) throws JsonMappingException, JsonProcessingException {

        Api api = null;

        if (port.hasApiDefinition() == false)
            return null;

        StandardDefinition apiStdDef = port.getPromises().getApi();
        Api newApi = new Api();
        newApi.setId(apiStdDef.getId());
        newApi.setFullyQualifiedName(apiStdDef.getFullyQualifiedName());
        newApi.setEntityType(apiStdDef.getEntityType());
        newApi.setName(apiStdDef.getName());
        newApi.setDisplayName(apiStdDef.getDisplayName());
        newApi.setVersion(apiStdDef.getVersion());
        newApi.setDescription(apiStdDef.getDescription());
        newApi.setSpecification(apiStdDef.getSpecification());
        newApi.setSpecificationVersion(apiStdDef.getSpecificationVersion());
        newApi.setDefinitionMediaType(apiStdDef.getDefinition().getMediaType());
        newApi.setDefinition(apiStdDef.getDefinition().getRawContent());

        
        try {
            api = apiService.searchDefinition(newApi);
            if (api == null) {
                api = apiService.createApi(newApi);
            }
            apiStdDef.getDefinition().setOriginalRef(apiStdDef.getDefinition().getRef());
            apiStdDef.getDefinition().setRef(serverUrl + "/apis/" + api.getId());
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving api of port [" + port.getFullyQualifiedName()
                            + "]",
                    t);
        }

        return api;
    }

    private void saveTemplates(DataProductVersion dataProductVersion, String serverUrl) throws JsonProcessingException {
        
        if (dataProductVersion.hasLifecycleInfo()) {
            saveLifecycleInfoTemplates(
                    dataProductVersion.getInternalComponents().getLifecycleInfo(), serverUrl);
        }
    }

      private void saveLifecycleInfoTemplates(LifecycleInfo lifecycleInfo, String serverUrl)
            throws JsonProcessingException {
    
        for (LifecycleActivityInfo activity : lifecycleInfo.getActivityInfos()) {
            if(activity.getTemplate() != null && activity.getTemplate().getDefinition() != null) {
                saveTemplate(activity.getTemplate(), serverUrl); 
            }
        }
    }

    private Template saveTemplate(
        TemplateStandardDefinition templateStdDef, String serverUrl)
    throws JsonProcessingException {

        

        Template template = new Template(); // why not a mapper?
        template.setId(templateStdDef.getId());
        template.setFullyQualifiedName(templateStdDef.getFullyQualifiedName());
        template.setEntityType(templateStdDef.getEntityType());
        template.setName(templateStdDef.getName());
        template.setDisplayName(templateStdDef.getDisplayName());
        template.setVersion(templateStdDef.getVersion());
        template.setDescription(templateStdDef.getDescription());
        template.setSpecification(templateStdDef.getSpecification());
        template.setSpecificationVersion(templateStdDef.getSpecificationVersion());
        template.setDefinitionMediaType(templateStdDef.getDefinition().getMediaType());
        template.setDefinition(templateStdDef.getDefinition().getRawContent());

        try {
            Template existingTemplate = templateService.searchDefinition(template);
            if (existingTemplate == null) {
                template = templateService.createTemplate(template);
            } else {
                template = existingTemplate;
            }

            templateStdDef.getDefinition().setOriginalRef(templateStdDef.getDefinition().getRef());
            templateStdDef.getDefinition().setRef(serverUrl + "/templates/" + template.getId());
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving template [" +  templateStdDef.getName() + "]", t);
        }
        
        return template;
    }

    

    

    // ======================================================================================
    // READ
    // ======================================================================================

    // readAllDataProductVersions()
    // is not implemented beacuse it make
    // non sense to read al versions across all possible data products.
    // @see searchDataProductVersions(dataProduct)

    private DataProductVersion readDataProductVersion(DataProductVersion dataProductVersion) {
        if (dataProductVersion == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Data product version object cannot be null");
        }

        if (dataProductVersion.getDataProduct() == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Data product object cannot be null");
        }
        return readDataProductVersion(dataProductVersion.getDataProduct().getId(),
                dataProductVersion.getInfo().getVersionNumber());
    }

    public DataProductVersion readDataProductVersion(String dataProductId, String version) {

        DataProductVersion dataProductVersion = null;

        if (!StringUtils.hasText(dataProductId)) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Data product id cannot be empty");
        }

        try {
            dataProductVersion = getDataProductVersion(dataProductId, version);
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading version [" + version + "] of data product ["
                            + dataProductId + "]",
                    t);
        }

        if (dataProductVersion == null) {
            throw new NotFoundException(
                RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                    "Data product [" + dataProductId + "] does not exist");
        }

        return dataProductVersion;
    }

    private DataProductVersion getDataProductVersion(String dataProductId, String version) {
        DataProductVersion dataProductVersion = null;

        Optional<DataProductVersion> dataProductVersionLookUpResults = dataProductVersionRepository
                .findByDataProductIdAndVersionNumber(dataProductId, version);
        if (dataProductVersionLookUpResults.isPresent()) {
            dataProductVersion = dataProductVersionLookUpResults.get();
        }
        return dataProductVersion;
    }

    // -------------------------
    // exists methods
    // -------------------------
    private boolean dataProductVersionExists(DataProductVersion dataProductVersion) {
        if (dataProductVersion == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Data product version object cannot be null");
        }

        if (dataProductVersion.getDataProduct() == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Data product object cannot be null");
        }

        return dataProductVersionExists(
                dataProductVersion.getDataProduct().getId(),
                dataProductVersion.getInfo().getVersionNumber());
    }

    private boolean dataProductVersionExists(String dataProductId, String version) {
        return getDataProductVersion(dataProductId, version) != null;
    }

    // -------------------------
    // search methods
    // -------------------------

    public List<DataProductVersion> searchDataProductVersions(String dataProductId) {
        List<DataProductVersion> dataProductVersionSearchResult = null;

        try {
            dataProductVersionSearchResult = findDataProductVersions(dataProductId);
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching data product versions",
                    t);
        }

        return dataProductVersionSearchResult;
    }

    private List<DataProductVersion> findDataProductVersions(String dataProductId) {
        return dataProductVersionRepository.findByDataProductId(dataProductId);
    }

    // -------------------------
    // GET Data Product Components methods
    // -------------------------

    public List<ApplicationComponent> searchDataProductVersionApplicationComponents(String dataProductId, String version) {
        DataProductVersion dataProductVersionSearchResult;
        try {
            dataProductVersionSearchResult = getDataProductVersion(dataProductId, version);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching data product versions",
                    t);
        }

        return dataProductVersionSearchResult.getInternalComponents().getApplicationComponents();
    }

    public List<InfrastructuralComponent> searchDataProductVersionInfrastructuralComponents(String dataProductId, String version) {
        DataProductVersion dataProductVersionSearchResult;
        try {
            dataProductVersionSearchResult = getDataProductVersion(dataProductId, version);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching data product versions",
                    t);
        }

        return dataProductVersionSearchResult.getInternalComponents().getInfrastructuralComponents();
    }

    // ======================================================================================
    // UPDATE
    // ======================================================================================

    // Data product versions are immutable objects

    // ======================================================================================
    // DELETE
    // ======================================================================================

    public void deleteDataProductVersion(DataProductVersion dataProductVersion) {
        if (dataProductVersion == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Data product version object cannot be null");
        }
        deleteDataProductVersion(
                dataProductVersion.getDataProductId(), dataProductVersion.getVersionNumber());
    }

    public void deleteAllDataProductVersions(String dataProductId) {
        List<DataProductVersion> dataProductVersions = searchDataProductVersions(dataProductId);

        for (DataProductVersion dataProductVersion : dataProductVersions) {
            deleteDataProductVersion(dataProductVersion);
        }
    }

    public void deleteDataProductVersion(String dataProductId, String versionNumber) {
        DataProductVersion dataProductVersion = readDataProductVersion(dataProductId, versionNumber);

        try {
            DataProductVersionId dataProductVersionId = new DataProductVersionId();
            dataProductVersionId.setDataProductId(dataProductId);
            dataProductVersionId.setVersionNumber(versionNumber);
            dataProductVersionRepository.deleteById(dataProductVersionId);
            //dataProductVersionRepository.delete(dataProductVersion);
            logger.info("Data product version [" + versionNumber + "] of data product [" + dataProductId
                    + "] succesfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while deleting data product version",
                    t
            );
        }

        try {
            // metaServiceProxy.uploadDataProductVersion(dataProductVersion);
            EventResource eventResource = new EventResource(
                    EventType.DATA_PRODUCT_VERSION_DELETED,
                    dataProductVersion.getDataProductId(),
                    dataProductVersionMapper.toResource(dataProductVersion).toEventString(),
                    null);
            eventNotifier.notifyEvent(eventResource);
        } catch (Throwable t) {
            throw new BadGatewayException(
                ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
                    "Impossible to upload data product version to metaService: " + t.getMessage(),
                    t
            );
        }

    }

    // ======================================================================================
    // OTHER
    // ======================================================================================

    public boolean isCompliantWithGlobalPolicies(DataProductVersion dataProductVersion) {
        Boolean isValid = false;

        try {
            isValid = policyServiceProxy.validateDataProductVersion(
                    dataProductVersion, PolicyName.dataproduct);
        } catch (Throwable t) {
            throw new BadGatewayException(
                ODMApiCommonErrors.SC502_71_POLICY_SERVICE_ERROR,
                    "An error occured while invoking policy service to validate data product version: " + t.getMessage(),
                    t
            );
        }

        return isValid;
    }

}
