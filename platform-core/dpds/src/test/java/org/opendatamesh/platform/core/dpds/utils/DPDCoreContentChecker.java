package org.opendatamesh.platform.core.dpds.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.regex.Pattern;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DPDCoreContentChecker implements ResourceContentChecker {

	@Override
	public JsonNode verifyAll(String rootEntityContent) {
		ObjectMapper mapper = ObjectMapperFactory.getRightMapper(rootEntityContent);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		JsonNode rootEntityNode = null;
		try {
			rootEntityNode = mapper.readTree(rootEntityContent);
		} catch (JsonProcessingException e) {
			fail("Impossible to parse response");
		}
		return verifyDescriptorContent(rootEntityNode);
	}

	@Override
	public JsonNode verifyDescriptorContent(JsonNode rootEntity) {

		assertThat(rootEntity).isNotNull();
		assertThat(rootEntity.get("dataProductDescriptor").asText())
				.isEqualTo("1.0.0");

		verifyInfoContent((ObjectNode) rootEntity.get("info"));

		verifyInterfaceComponentsContent((ObjectNode) rootEntity.get("interfaceComponents"));

		JsonNode internalComponentsObject = rootEntity.get("internalComponents");
		assertThat(internalComponentsObject).isNotNull();
		verifyApplicationComponentsContent((ArrayNode) internalComponentsObject.get("applicationComponents"));
		verifyInfrastructuralComponentsContent((ArrayNode) internalComponentsObject.get("infrastructuralComponents"));
		verifyLifecycleContent((ObjectNode)internalComponentsObject.get("lifecycleInfo"));

		return rootEntity;
	}

	@Override
	public JsonNode verifyInfoContent(ObjectNode infoNode) {

		assertThat(infoNode).isNotNull();
		assertThat(get(infoNode, "id")).isEqualTo("f350cab5-992b-32f7-9c90-79bca1bf10be");
		assertThat(get(infoNode, "fullyQualifiedName")).isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore");
		assertThat(get(infoNode, "entityType")).isEqualTo(EntityTypeDPDS.DATAPRODUCT.propertyValue());
		assertThat(get(infoNode, "name")).isEqualTo("dpdCore");
		assertThat(get(infoNode, "version")).isEqualTo("1.0.0");
		assertThat(get(infoNode, "displayName")).isEqualTo("Test Product");
		assertThat(get(infoNode, "description"))
				.isEqualTo("This is a test product that contains the core configurations blocks");
		assertThat(get(infoNode, "domain")).isEqualTo("testDomain");
		assertThat(get(infoNode, "x-prop")).isEqualTo("x-prop-value");

		ObjectNode ownerNode = (ObjectNode) infoNode.get("owner");
		assertThat(ownerNode).isNotNull();
		assertThat(get(ownerNode, "id")).isEqualTo("john.doe@company-xyz.com");
		assertThat(get(ownerNode, "name")).isEqualTo("John Doe");

		return infoNode;
	}

	@Override
	public JsonNode verifyInterfaceComponentsContent(ObjectNode interfaceComponentsNode) {

		assertThat(interfaceComponentsNode).isNotNull();

		// Input ports
		JsonNode inputPortNodes = interfaceComponentsNode.path("inputPorts");
		assertThat(inputPortNodes).isNotNull();

		assertThat(inputPortNodes.isArray()).isTrue();
		assertThat(inputPortNodes.size()).isEqualTo(1);
		ObjectNode inputPortNode = (ObjectNode) inputPortNodes.get(0);
		assertThat(inputPortNode).isNotNull();

		assertThat(get(inputPortNode, "id")).isEqualTo("2915c611-317b-3464-b0b8-16569ef5b771");
		assertThat(get(inputPortNode, "fullyQualifiedName"))
				.isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:inputports:inputPortA:1.0.0");
		assertThat(get(inputPortNode, "entityType")).isEqualTo(EntityTypeDPDS.INPUTPORT.propertyValue());
		assertThat(get(inputPortNode, "name")).isEqualTo("inputPortA");
		assertThat(get(inputPortNode, "version")).isEqualTo("1.0.0");
		assertThat(get(inputPortNode, "displayName")).isEqualTo("Input port A");
		assertThat(get(inputPortNode, "description")).isEqualTo("Input port A of data product");
		assertThat(get(inputPortNode, "componentGroup")).isEqualTo("gruppoA");
		assertThat(get(inputPortNode, "x-prop")).isEqualTo("x-prop-value");

		ObjectNode promisesNode = (ObjectNode) inputPortNode.get("promises");
		assertThat(promisesNode).isNotNull();
		assertThat(get(promisesNode, "platform")).isEqualTo("platformX");
		assertThat(get(promisesNode, "servicesType")).isEqualTo("rest-services");
		assertThat(get(promisesNode, "x-prop")).isEqualTo("x-prop-value");

		
		ObjectNode apiNode = (ObjectNode) promisesNode.get("api");
		assertThat(apiNode).isNotNull();
		assertThat(get(apiNode, "name")).isEqualTo("restApi1");
		assertThat(get(apiNode, "version")).isEqualTo("1.3.2");
		assertThat(get(apiNode, "description")).isEqualTo("Rest input API");
		assertThat(get(apiNode, "specification")).isEqualTo("custom-api-spec");
		assertThat(get(apiNode, "specificationVersion")).isEqualTo("1.0.0");
		assertThat(get(apiNode, "x-prop")).isEqualTo("x-prop-value");

		ObjectNode apiDefinitionNode = (ObjectNode) apiNode.get("definition");
		assertThat(apiDefinitionNode).isNotNull();
		//assertThat(get(apiDefinitionNode, "$ref")).matches(Pattern.compile("http://localhost:\\d*/api/v1/pp/registry/apis/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"));
		
		// Output ports
		JsonNode outputPortNodes = interfaceComponentsNode.path("outputPorts");
		assertThat(outputPortNodes).isNotNull();
		assertThat(outputPortNodes.isArray()).isTrue();
		assertThat(outputPortNodes.size()).isEqualTo(1);

		ObjectNode outputPortNode = (ObjectNode) outputPortNodes.get(0);
		assertThat(outputPortNode).isNotNull();

		assertThat(get(outputPortNode, "id")).isEqualTo("7a6d1c54-e402-3a42-91fb-b793fae93153");
		assertThat(get(outputPortNode, "fullyQualifiedName"))
				.isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:outputports:outputPortA:1.0.0");
		assertThat(get(outputPortNode, "entityType")).isEqualTo(EntityTypeDPDS.OUTPUTPORT.propertyValue());
		assertThat(get(outputPortNode, "name")).isEqualTo("outputPortA");
		assertThat(get(outputPortNode, "version")).isEqualTo("1.0.0");
		assertThat(get(outputPortNode, "displayName")).isEqualTo("Output port A");
		assertThat(get(outputPortNode, "description")).isEqualTo("Output port A of data product");
		assertThat(get(outputPortNode, "componentGroup")).isEqualTo("gruppoA");
		assertThat(get(outputPortNode, "x-prop")).isEqualTo("x-prop-value");

		promisesNode = (ObjectNode) outputPortNode.get("promises");
		assertThat(promisesNode).isNotNull();
		assertThat(get(promisesNode, "platform")).isEqualTo("platformX");
		assertThat(get(promisesNode, "servicesType")).isEqualTo("rest-services");
		assertThat(get(promisesNode, "x-prop")).isEqualTo("x-prop-value");

		apiNode = (ObjectNode) promisesNode.get("api");
		assertThat(apiNode).isNotNull();
		assertThat(get(apiNode, "name")).isEqualTo("restApi2");
		assertThat(get(apiNode, "version")).isEqualTo("1.3.2");
		assertThat(get(apiNode, "description")).isEqualTo("Rest output API");
		assertThat(get(apiNode, "specification")).isEqualTo("custom-api-spec");
		assertThat(get(apiNode, "specificationVersion")).isEqualTo("1.0.0");
		assertThat(get(apiNode, "x-prop")).isEqualTo("x-prop-value");

		apiDefinitionNode = (ObjectNode) apiNode.get("definition");
		assertThat(apiDefinitionNode).isNotNull();
		assertThat(get(apiDefinitionNode, "$ref")).matches(Pattern.compile("http://localhost:\\d*/api/v1/pp/registry/apis/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"));

		return interfaceComponentsNode;
	}

	@Override
	public JsonNode verifyApplicationComponentsContent(ArrayNode appComponentNodes) {
		assertThat(appComponentNodes).isNotNull();
		assertThat(appComponentNodes.size()).isEqualTo(1);

		ObjectNode appNode = (ObjectNode) appComponentNodes.get(0);
		assertThat(appNode).isNotNull();
		assertThat(get(appNode, "id")).isEqualTo("33391f55-2127-391a-80c8-881c95ed7082");
		assertThat(get(appNode, "fullyQualifiedName"))
				.isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:applications:appA:1.1.0");
		assertThat(get(appNode, "entityType")).isEqualTo(EntityTypeDPDS.APPLICATION.propertyValue());
		assertThat(get(appNode, "name")).isEqualTo("appA");
		assertThat(get(appNode, "version")).isEqualTo("1.1.0");
		assertThat(get(appNode, "displayName")).isEqualTo("Application A");
		assertThat(get(appNode, "description")).isEqualTo("Internal application A of data product");
		assertThat(get(appNode, "platform")).isEqualTo("platformY");
		assertThat(get(appNode, "applicationType")).isEqualTo("spring-boot-app");
		assertThat(get(appNode, "componentGroup")).isEqualTo("gruppoB");
		assertThat(get(appNode, "x-prop")).isEqualTo("x-prop-value");
		assertThat(appNode.get("dependsOn")).isNotNull();
		assertThat(appNode.get("dependsOn").isArray()).isTrue();
		assertThat(appNode.get("dependsOn").size()).isEqualTo(1);
		assertThat(appNode.get("dependsOn").get(0)).isNotNull();
		assertThat(appNode.get("dependsOn").get(0).asText())
				.isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:infrastructures:infraA:1.1.0");

		return appComponentNodes;
	}

	@Override
	public JsonNode verifyInfrastructuralComponentsContent(ArrayNode infraComponents) {

		assertThat(infraComponents).isNotNull();
		assertThat(infraComponents.size()).isEqualTo(1);

		ObjectNode infraNode = (ObjectNode) infraComponents.get(0);
		assertThat(infraNode).isNotNull();
		assertThat(get(infraNode, "id")).isEqualTo("d1d74ccd-0a90-3c13-8bb5-6628fec44fbe");
		assertThat(get(infraNode, "fullyQualifiedName"))
				.isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:infrastructures:infraA:1.1.0");
		assertThat(get(infraNode, "entityType")).isEqualTo(EntityTypeDPDS.INFRASTRUCTURE.propertyValue());
		assertThat(get(infraNode, "name")).isEqualTo("infraA");
		assertThat(get(infraNode, "version")).isEqualTo("1.1.0");
		assertThat(get(infraNode, "displayName")).isEqualTo("Infra A");
		assertThat(get(infraNode, "description")).isEqualTo("Infrastructure component A of data product");
		assertThat(get(infraNode, "platform")).isEqualTo("platformX");
		assertThat(get(infraNode, "infrastructureType")).isEqualTo("storage-resource");
		assertThat(get(infraNode, "componentGroup")).isEqualTo("gruppoC");
		assertThat(get(infraNode, "x-prop")).isEqualTo("x-prop-value");

		return infraComponents;
	}

	@Override
	public JsonNode verifyLifecycleContent(ObjectNode lifecycleNode) {
		assertThat(lifecycleNode).isNotNull();
		ArrayNode stageTasksNode = null;
		ObjectNode taskNode = null, serviceNode = null, templateNode = null, templateDefNode = null, confNode = null;

		// TEST
		stageTasksNode = (ArrayNode) lifecycleNode.get("test");
		assertThat(stageTasksNode).isNotNull();
		taskNode = (ObjectNode)stageTasksNode.get(0);
		assertThat(taskNode).isNotNull();
		serviceNode = (ObjectNode) taskNode.get("service");
		assertThat(serviceNode).isNotNull();
		assertThat(get(serviceNode, "$href")).isEqualTo("{azure-devops}");
		
		
		templateNode = (ObjectNode) taskNode.get("template");
		assertThat(templateNode).isNotNull();
		assertThat(get(templateNode, "name")).isEqualTo("testPipeline");
		assertThat(get(templateNode, "version")).isEqualTo("1.0.0");
		assertThat(get(templateNode, "specification")).isEqualTo("azure-devops");
		assertThat(get(templateNode, "specificationVersion")).isEqualTo("1.0.0");

		templateDefNode = (ObjectNode)templateNode.get("definition");
		assertThat(templateDefNode).isNotNull();
		assertThat(get(templateDefNode, "originaRef")).isNull();
		assertThat(get(templateDefNode, "$ref")).matches(Pattern.compile("http://localhost:\\d*/api/v1/pp/registry/templates/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"));

		confNode = (ObjectNode) taskNode.get("configurations");
		assertThat(confNode).isNotNull();
		assertThat(confNode.get("stagesToSkip")).isNotNull();
		assertThat(confNode.get("stagesToSkip").isArray()).isTrue();
		assertThat(confNode.get("stagesToSkip").size()).isEqualTo(1);
		assertThat(confNode.get("stagesToSkip").get(0)).isNotNull();
		assertThat(confNode.get("stagesToSkip").get(0).asText()).isEqualTo("Deploy");

		// PROD
		stageTasksNode = (ArrayNode) lifecycleNode.get("prod");
		assertThat(stageTasksNode).isNotNull();
		taskNode = (ObjectNode)stageTasksNode.get(0);
		assertThat(taskNode).isNotNull();
		serviceNode = (ObjectNode) taskNode.get("service");
		assertThat(serviceNode).isNotNull();
		assertThat(get(serviceNode, "$href")).isEqualTo("{azure-devops}");
		
		
		templateNode = (ObjectNode) taskNode.get("template");
		assertThat(templateNode).isNotNull();
		assertThat(get(templateNode, "name")).isEqualTo("testPipeline");
		assertThat(get(templateNode, "version")).isEqualTo("1.0.0");
		assertThat(get(templateNode, "specification")).isEqualTo("azure-devops");
		assertThat(get(templateNode, "specificationVersion")).isEqualTo("1.0.0");

		templateDefNode = (ObjectNode)templateNode.get("definition");
		assertThat(templateDefNode).isNotNull();
		assertThat(get(templateDefNode, "originaRef")).isNull();
		assertThat(get(templateDefNode, "$ref")).matches(Pattern.compile("http://localhost:\\d*/api/v1/pp/registry/templates/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"));

		confNode = (ObjectNode) taskNode.get("configurations");
		assertThat(confNode).isNotNull();
		assertThat(confNode.get("stagesToSkip")).isNotNull();
		assertThat(confNode.get("stagesToSkip").isArray()).isTrue();
		assertThat(confNode.get("stagesToSkip").size()).isEqualTo(0);

		// DEPRECATED
		stageTasksNode = (ArrayNode) lifecycleNode.get("deprecated");
		assertThat(stageTasksNode).isNotNull();
		taskNode = (ObjectNode)stageTasksNode.get(0);
		assertThat(taskNode).isNotNull();
		assertThat(taskNode.get("service")).isNull();
		assertThat(taskNode.get("template")).isNull();
		assertThat(taskNode.get("configurations")).isNull();

		return lifecycleNode;
	}

	private static String get(ObjectNode node, String propertyName) {
		JsonNode propertyValue = node.get(propertyName);
		return propertyValue == null ? null : propertyValue.asText();
	}
}
