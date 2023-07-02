package org.opendatamesh.platform.pp.registry.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.*;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.*;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiToSchemaRelationship.ApiToSchemaRelationshipId;
import org.opendatamesh.platform.pp.registry.database.repositories.DataProductVersionRepository;
import org.opendatamesh.platform.pp.registry.exceptions.*;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.observers.EventNotifier;
import org.opendatamesh.platform.pp.registry.resources.v1.policyservice.PolicyName;
import org.opendatamesh.platform.up.notification.api.v1.resources.EventResource;
import org.opendatamesh.platform.up.notification.api.v1.resources.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class DataProductVersionService {

    @Autowired
    private DataProductVersionRepository dataProductVersionRepository;

    @Autowired
    private  DefinitionService definitionService;

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

    protected DataProductVersion createDataProductVersion(DataProductVersion dataProductVersion) {
        return createDataProductVersion(dataProductVersion, true);
    }

    protected DataProductVersion createDataProductVersion(DataProductVersion dataProductVersion,
            boolean checkGlobalPolicies) {
        if (dataProductVersion == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Data product version object cannot be null");
        }

        if (dataProductVersionExists(dataProductVersion)) {
            dataProductVersion.getInfo().getVersionNumber();
            dataProductVersion.getInfo().getFullyQualifiedName();
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_05_VERSION_ALREADY_EXISTS,
                    "Version [" + dataProductVersion.getInfo().getVersionNumber() + "] of data product ["
                            + dataProductVersion.getInfo().getFullyQualifiedName() + "] already exists");
        }

        // TODO check schemas evolution rules
        if (checkGlobalPolicies && !isCompliantWithGlobalPolicies(dataProductVersion)) {
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
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
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
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "Impossible to upload data product version to metaService", t);
        }

        return dataProductVersion;
    }

    public DataProductVersion saveDataProductVersion(DataProductVersion dataProductVersion) {
        return dataProductVersionRepository.saveAndFlush(dataProductVersion);
    }

    private void saveApiDefinitions(DataProductVersion dataProductVersion)
            throws JsonProcessingException {
        if (dataProductVersion != null && dataProductVersion.getInterfaceComponents() != null) {
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getInputPorts());
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getOutputPorts());
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getDiscoveryPorts());
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getObservabilityPorts());
            saveApiDefinitions(dataProductVersion.getInterfaceComponents().getControlPorts());
        }
    }

    private void saveApiDefinitions(List<Port> ports) throws JsonProcessingException {
        if (ports == null || ports.size() == 0)
            return;

        for (Port port : ports) {
            Map<ApiDefinitionEndpoint, Schema> schemas = saveApiSchemas(port);
            Definition apiDefinition = saveApiDefinition(port);
            saveApiToSchemaRelationship(apiDefinition, schemas);
        }
    }

    private void saveApiToSchemaRelationship(Definition apiDefinition, Map<ApiDefinitionEndpoint, Schema> schemas) {
        for(Map.Entry<ApiDefinitionEndpoint, Schema> entry: schemas.entrySet()) {
            ApiToSchemaRelationship relationship = new ApiToSchemaRelationship();
            relationship.setId(new ApiToSchemaRelationshipId(apiDefinition.getId(), entry.getValue().getId()));
            
            relationship.setOperationId(entry.getKey().getName());
            relationship.setOutputMediaType(entry.getKey().getOutputMediaType());
            if(schemaService.searchRelationship(relationship.getId()) == null) {
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
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while schema of endpoint [" + endpoint
                            + "]",
                    t);
        }

        // rewrite schema ref withing api def
    
        return schema;    
    }

    private Definition saveApiDefinition(Port port) throws JsonMappingException, JsonProcessingException {

        Definition definition = null;

        if (port.getPromises() == null)
            return null;
        if (port.getPromises().getApi() == null)
            return null;

        StandardDefinition api = port.getPromises().getApi();
        Definition newDefinition = new Definition(); // why not a mapper?
        newDefinition.setName(api.getName());
        newDefinition.setVersion(api.getVersion());
        newDefinition.setDescription(api.getDescription());
        newDefinition.setSpecification(api.getSpecification());
        newDefinition.setSpecificationVersion(api.getSpecificationVersion());
        newDefinition.setContent(api.getDefinition().getRawContent());
        
        // Api is created first to obtain the id used after for replacing api definition
        // content with a reference url
        try {
            definition = definitionService.searchDefinition(newDefinition);
            if(definition == null) {
                definition = definitionService.createDefinition(newDefinition);
            }
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving api of port [" + port.getFullyQualifiedName()
                            + "]",
                    t);
        }

        // Once we have the api id we replace the definition content with a reference url
        ObjectNode portObject = (ObjectNode) objectMapper.readTree(port.getRawContent());
        
        ObjectNode standardDefinitionContent = (ObjectNode) portObject.at("/promises/api/definition");
        String ref = String.valueOf(standardDefinitionContent.get("$ref"));
        ref = ref.replaceAll("\\{apiId\\}", "" + definition.getId());
        ref = ref.replaceAll("\"", "");
        standardDefinitionContent.put("$ref", ref);
        
        port.setRawContent(objectMapper.writeValueAsString(portObject));

        port.getPromises().setApiId(definition.getId());

        return definition;
    }

    private void saveTemplates(DataProductVersion dataProductVersion) throws JsonProcessingException {
        if( dataProductVersion!= null && dataProductVersion.getInternalComponents() != null) {
            saveInfrastructuralComponentTemplates(dataProductVersion.getInternalComponents().getInfrastructuralComponents());
            saveApplicationComponentTemplates(dataProductVersion.getInternalComponents().getApplicationComponents());
        }
    }

    private void saveInfrastructuralComponentTemplates(List<InfrastructuralComponent> components) throws JsonProcessingException {
        if(components == null || components.size() == 0) return;

        for(InfrastructuralComponent component: components) {
            Template template = saveInfrastructuralComponentTemplate(component);
            if (template != null){
                //saveComponentTemplateRelationship(template, component, "infrastructuralComponent", "provisionInfo");
            }
                
        }
    }

    private void saveApplicationComponentTemplates(List<ApplicationComponent> components) throws JsonProcessingException {
        if(components == null || components.size() == 0) return;

        for(ApplicationComponent component : components) {
            Template buildTemplate = saveApplicationComponentBuildInfoTemplates(component);
            if (buildTemplate != null) {
                //saveComponentTemplateRelationship(buildTemplate, component, "applicationComponent", "buildInfo");
            }
                
            Template deployTemplate = saveApplicationComponentDeployInfoTemplates(component);
            if (deployTemplate != null) {
                //saveComponentTemplateRelationship(deployTemplate, component, "applicationComponent", "deployInfo");
            }
                
        }
    }

    private Template saveInfrastructuralComponentTemplate(InfrastructuralComponent component) throws JsonProcessingException {

        if (component.getProvisionInfo() == null)
            return null;
        if (
                component.getProvisionInfo().getTemplate() == null || (
                        component.getProvisionInfo().getTemplate().getDescription() == null
                                && component.getProvisionInfo().getTemplate().getMediaType() == null
                                && component.getProvisionInfo().getTemplate().getRef() == null
                )
        ) {
            ObjectNode componentObject = (ObjectNode) objectMapper.readTree(component.getRawContent());
            ObjectNode templateContent = (ObjectNode) componentObject.at("/provisionInfo/template");
            templateContent.put("$ref", "");
            component.setRawContent(objectMapper.writeValueAsString(componentObject));
            return null;
        }

        ReferenceObject template = component.getProvisionInfo().getTemplate();
        Template templateEntity = saveTemplate(template, component.getFullyQualifiedName());

        // Once we have the api id we replace the definition content with a reference url
        ObjectNode componentObject = (ObjectNode) objectMapper.readTree(component.getRawContent());
        ObjectNode templateContent = (ObjectNode) componentObject.at("/provisionInfo/template");
        String ref = String.valueOf(templateContent.get("$ref"));
        ref = ref.replaceAll("\\{templateId\\}", "" + templateEntity.getId());
        ref = ref.replaceAll("\"", "");
        templateContent.put("$ref", ref);
        component.setRawContent(objectMapper.writeValueAsString(componentObject));

        return templateEntity;

    }

    private Template saveApplicationComponentBuildInfoTemplates(ApplicationComponent component) throws JsonProcessingException {

        if (component.getBuildInfo() == null)
            return null;
        if (
                component.getBuildInfo().getTemplate() == null || (
                        component.getBuildInfo().getTemplate().getDescription() == null
                                && component.getBuildInfo().getTemplate().getMediaType() == null
                                && component.getBuildInfo().getTemplate().getRef() == null
                )
        ) {
            ObjectNode componentObject = (ObjectNode) objectMapper.readTree(component.getRawContent());
            ObjectNode templateContent = (ObjectNode) componentObject.at("/buildInfo/template");
            templateContent.put("$ref", "");
            component.setRawContent(objectMapper.writeValueAsString(componentObject));
            return null;
        }

        ReferenceObject template = component.getBuildInfo().getTemplate();
        Template templateEntity = saveTemplate(template, component.getFullyQualifiedName());

        // Once we have the api id we replace the definition content with a reference url
        ObjectNode componentObject = (ObjectNode) objectMapper.readTree(component.getRawContent());
        ObjectNode templateContent = (ObjectNode) componentObject.at("/buildInfo/template");
        String ref = String.valueOf(templateContent.get("$ref"));
        ref = ref.replaceAll("\\{templateId\\}", "" + templateEntity.getId());
        ref = ref.replaceAll("\"", "");
        templateContent.put("$ref", ref);
        component.setRawContent(objectMapper.writeValueAsString(componentObject));

        return templateEntity;

    }

    private Template saveApplicationComponentDeployInfoTemplates(ApplicationComponent component) throws JsonProcessingException {

        if (component.getDeployInfo() == null)
            return null;
        if (
                component.getDeployInfo().getTemplate() == null || (
                        component.getDeployInfo().getTemplate().getDescription() == null
                        && component.getDeployInfo().getTemplate().getMediaType() == null
                        && component.getDeployInfo().getTemplate().getRef() == null
                )
        ) {
            ObjectNode componentObject = (ObjectNode) objectMapper.readTree(component.getRawContent());
            ObjectNode templateContent = (ObjectNode) componentObject.at("/deployInfo/template");
            templateContent.put("$ref", "");
            component.setRawContent(objectMapper.writeValueAsString(componentObject));
            return null;
        }

        ReferenceObject template = component.getDeployInfo().getTemplate();
        Template templateEntity = saveTemplate(template, component.getFullyQualifiedName());

        // Once we have the api id we replace the definition content with a reference url
        ObjectNode componentObject = (ObjectNode) objectMapper.readTree(component.getRawContent());
        ObjectNode templateContent = (ObjectNode) componentObject.at("/deployInfo/template");
        String ref = String.valueOf(templateContent.get("$ref"));
        ref = ref.replaceAll("\\{templateId\\}", "" + templateEntity.getId());
        ref = ref.replaceAll("\"", "");
        templateContent.put("$ref", ref);
        component.setRawContent(objectMapper.writeValueAsString(componentObject));

        return templateEntity;

    }

    private Template saveTemplate(ReferenceObject template, String componentName) {

        Template templateEntity = null;

        try {
            if(StringUtils.hasText(template.getMediaType())
                    && StringUtils.hasText(template.getRef())) {
                templateEntity = templateService.searchTemplate(template.getMediaType(), template.getRef());
            }
            if(templateEntity == null) {
                templateEntity = templateService.createTemplate(
                        new Template(
                                template.getDescription(),
                                template.getMediaType(),
                                template.getRef()
                        )
                );
            }

        } catch(Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving template of component [" +  componentName + "]",
                    t);
        }

        return templateEntity;
    }

    private void saveComponentTemplateRelationship(Template template, Component component, String componentType, String infoType) {
        ComponentTemplate relationship = new ComponentTemplate();
        relationship.setId(new ComponentTemplate.ComponentTemplateId(
                UUID.nameUUIDFromBytes(component.getFullyQualifiedName().getBytes()).toString(),
                template.getId(),
                componentType,
                infoType
        ));
        templateService.createComponentTemplateRelationship(relationship);
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
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Data product version object cannot be null");
        }

        if (dataProductVersion.getDataProduct() == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Data product object cannot be null");
        }
        return readDataProductVersion(dataProductVersion.getDataProduct().getId(),
                dataProductVersion.getInfo().getVersionNumber());
    }

    public DataProductVersion readDataProductVersion(String dataProductId, String version) {

        DataProductVersion dataProductVersion = null;

        if (!StringUtils.hasText(dataProductId)) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Data product id cannot be empty");
        }

        try {
            dataProductVersion = getDataProductVersion(dataProductId, version);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading version [" + version + "] of data product ["
                            + dataProductId + "]",
                    t);
        }

        if (dataProductVersion == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_01_PRODUCT_NOT_FOUND,
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
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Data product version object cannot be null");
        }

        if (dataProductVersion.getDataProduct() == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
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
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching data product versions",
                    t);
        }

        return dataProductVersionSearchResult;
    }

    private List<DataProductVersion> findDataProductVersions(String dataProductId) {
        return dataProductVersionRepository.findByDataProductId(dataProductId);
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
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
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
            dataProductVersionRepository.delete(dataProductVersion);
            logger.info("Data product version [" + versionNumber + "] of data product [" + dataProductId
                    + "] succesfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while deleting data product version",
                    t);
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
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "Impossible to upload data product version to metaService", t);
        }

    }

    // ======================================================================================
    // OTHER
    // ======================================================================================

    public boolean isCompliantWithGlobalPolicies(DataProductVersion dataProductVersion) {
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
