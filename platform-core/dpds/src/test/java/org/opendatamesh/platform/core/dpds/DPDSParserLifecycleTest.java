package org.opendatamesh.platform.core.dpds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DPDSParserLifecycleTest extends DPDSTests {

   

    @Test
    public void lifecycleDpdTest() {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_LIFECYCLE.getContentLocation();
        } catch (IOException e) {
            fail("Impossible to get descriptor location", e);
        }

        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");

        ParseResult result = null;

        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }

        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
        LifecycleActivityInfoDPDS activityInfo = null;
        ObjectNode templateDefinitionNode = null;

        assertTrue(descriptor != null);
        assertNotNull(descriptor.getInternalComponents());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo().getActivityInfos());
        assertEquals(2, descriptor.getInternalComponents().getLifecycleInfo().getActivityInfos().size());

        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("dev");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getService());
        assertEquals("azure-devops", activityInfo.getService().getHref());
        assertNotNull(activityInfo.getTemplate());
        assertEquals("spec", activityInfo.getTemplate().getSpecification());
        assertEquals("2.0", activityInfo.getTemplate().getSpecificationVersion());
        assertNotNull(activityInfo.getTemplate().getDefinition());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getDescription());
        assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(activityInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());
        assertNotNull(activityInfo.getConfigurations());
        assertEquals(1, activityInfo.getConfigurations().size());
        assertEquals("DEV", activityInfo.getConfigurations().get("stage"));

        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("prod");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getService());
        assertEquals("azure-devops", activityInfo.getService().getHref());
        assertNotNull(activityInfo.getTemplate());
        assertEquals("spec", activityInfo.getTemplate().getSpecification());
        assertEquals("2.0", activityInfo.getTemplate().getSpecificationVersion());
        assertNotNull(activityInfo.getTemplate().getDefinition());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getDescription());
        assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(activityInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());
        assertNotNull(activityInfo.getConfigurations());
        assertEquals(1, activityInfo.getConfigurations().size());
        assertEquals("PROD", activityInfo.getConfigurations().get("stage"));
    }

    @Test
    public void lifecycleDpdEmptyTest() {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_LIFECYCLE_EMPTY.getContentLocation();
        } catch (IOException e) {
            fail("Impossible to get descriptor location", e);
        }

        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");

        ParseResult result = null;

        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }

        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        LifecycleActivityInfoDPDS activityInfo = null;
        ObjectNode templateDefinitionNode = null;

        assertTrue(descriptor != null);
        assertNotNull(descriptor.getInternalComponents());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo().getActivityInfos());
        assertEquals(4, descriptor.getInternalComponents().getLifecycleInfo().getActivityInfos().size());

        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("dev");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getService());
        assertEquals(null, activityInfo.getTemplate());
        assertEquals(null, activityInfo.getConfigurations());

        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("qa");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getService());
        assertNotNull(activityInfo.getTemplate());
        assertEquals(null, activityInfo.getConfigurations());

        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("prod");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getService());
        assertNotNull(activityInfo.getTemplate());
        assertNotNull(activityInfo.getConfigurations());

        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("deprecated");
        assertNotNull(activityInfo);
        assertEquals(null, activityInfo.getService());
        assertEquals(null, activityInfo.getTemplate());
        assertEquals(null, activityInfo.getConfigurations());

    }

    @Test
    public void lifecycleDpdExternalRefTest() {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_LIFECYCLE_EREF.getUriLocation();
        } catch (Exception e) {
            fail("Impossible to get descriptor location", e);
        }

        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");

        ParseResult result = null;

        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }

        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        LifecycleActivityInfoDPDS activityInfo = null;
        ObjectNode templateDefinitionNode = null;

        assertTrue(descriptor != null);
        assertNotNull(descriptor.getInternalComponents());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo().getActivityInfos());
        assertEquals(4, descriptor.getInternalComponents().getLifecycleInfo().getActivityInfos().size());

        //DEV
        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("dev");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getTemplate());
        assertEquals("spec", activityInfo.getTemplate().getSpecification());
        assertEquals("2.0", activityInfo.getTemplate().getSpecificationVersion());
        assertNotNull(activityInfo.getTemplate().getDefinition());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getDescription());
        assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        assertEquals("http://localhost:80/templates/{templateId}",
                activityInfo.getTemplate().getDefinition().getRef());
        assertEquals("template.json", activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(activityInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // QA
        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("qa");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getTemplate());
        assertNotNull(activityInfo.getTemplate());
        assertEquals("spec", activityInfo.getTemplate().getSpecification());
        assertEquals("2.0", activityInfo.getTemplate().getSpecificationVersion());
        assertNotNull(activityInfo.getTemplate().getDefinition());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getDescription());
        assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        assertEquals("http://localhost:80/templates/{templateId}",
                activityInfo.getTemplate().getDefinition().getRef());
        assertEquals("template.json", activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(activityInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // PROD
        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("prod");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getTemplate());
        assertNotNull(activityInfo.getTemplate());
        assertEquals("spec", activityInfo.getTemplate().getSpecification());
        assertEquals("2.0", activityInfo.getTemplate().getSpecificationVersion());
        assertNotNull(activityInfo.getTemplate().getDefinition());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getDescription());
        assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        assertEquals("http://localhost:80/templates/{templateId}",
                activityInfo.getTemplate().getDefinition().getRef());
        assertEquals("template.json", activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(activityInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // DEPRECATED
        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("deprecated");
        assertNotNull(activityInfo);
        assertEquals(null, activityInfo.getService());
        assertEquals(null, activityInfo.getTemplate());
        assertEquals(null, activityInfo.getConfigurations());
    }

    @Test
    public void lifecycleDpdInternalRefTest() {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_LIFECYCLE_IREF.getUriLocation();
        } catch (Exception e) {
            fail("Impossible to get descriptor location", e);
        }

        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");

        ParseResult result = null;

        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }

        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        LifecycleActivityInfoDPDS activityInfo = null;
        ObjectNode templateDefinitionNode = null;

        assertTrue(descriptor != null);
        assertNotNull(descriptor.getInternalComponents());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo().getActivityInfos());
        assertEquals(4, descriptor.getInternalComponents().getLifecycleInfo().getActivityInfos().size());

        //DEV
        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("dev");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getTemplate());
        assertEquals("spec", activityInfo.getTemplate().getSpecification());
        assertEquals("2.0", activityInfo.getTemplate().getSpecificationVersion());
        assertNotNull(activityInfo.getTemplate().getDefinition());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getDescription());
        assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());

        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(activityInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // QA
        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("qa");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getTemplate());
        assertNotNull(activityInfo.getTemplate());
        assertEquals("spec", activityInfo.getTemplate().getSpecification());
        assertEquals("2.0", activityInfo.getTemplate().getSpecificationVersion());
        assertNotNull(activityInfo.getTemplate().getDefinition());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getDescription());
        assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(activityInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // PROD
        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("prod");
        assertNotNull(activityInfo);
        assertNotNull(activityInfo.getTemplate());
        assertNotNull(activityInfo.getTemplate());
        assertEquals("spec", activityInfo.getTemplate().getSpecification());
        assertEquals("2.0", activityInfo.getTemplate().getSpecificationVersion());
        assertNotNull(activityInfo.getTemplate().getDefinition());
        assertEquals(null, activityInfo.getTemplate().getDefinition().getDescription());
        assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(activityInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());



        activityInfo = descriptor.getInternalComponents().getLifecycleInfo().getActivityInfo("deprecated");
        assertNotNull(activityInfo);
        assertEquals(null, activityInfo.getService());
        assertEquals(null, activityInfo.getTemplate());
        assertEquals(null, activityInfo.getConfigurations());
    }

}
