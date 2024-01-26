package org.opendatamesh.platform.core.dpds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleTaskInfoDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.utils.DPDSTestResources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DPDSParserLifecycleTest extends DPDSTests {

    @Test
    public void lifecycleDpdTest() {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_LIFECYCLE.getContentLocation();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location", t);
        }

        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");

        ParseResult result = null;

        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }

        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
        LifecycleTaskInfoDPDS taskInfo = null;
        ObjectNode templateDefinitionNode = null;

        assertTrue(descriptor != null);
        assertNotNull(descriptor.getInternalComponents());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo());
        assertEquals(2, descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo().size());

        List<LifecycleTaskInfoDPDS> tasksInfo = null;

        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("dev");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getService());
        assertEquals("{azure-devops}", taskInfo.getService().getHref());
        assertNotNull(taskInfo.getTemplate());
        assertEquals("spec", taskInfo.getTemplate().getSpecification());
        assertEquals("2.0", taskInfo.getTemplate().getSpecificationVersion());
        assertNotNull(taskInfo.getTemplate().getDefinition());
        assertEquals(null, taskInfo.getTemplate().getDefinition().getDescription());
        //assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        //assertEquals(null, activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(taskInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());
        assertNotNull(taskInfo.getConfigurations());
        assertEquals(1, taskInfo.getConfigurations().size());
        assertEquals("DEV", taskInfo.getConfigurations().get("stage"));

        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("prod");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getService());
        assertEquals("{azure-devops}", taskInfo.getService().getHref());
        assertNotNull(taskInfo.getTemplate());
        assertEquals("spec", taskInfo.getTemplate().getSpecification());
        assertEquals("2.0", taskInfo.getTemplate().getSpecificationVersion());
        assertNotNull(taskInfo.getTemplate().getDefinition());
        assertEquals(null, taskInfo.getTemplate().getDefinition().getDescription());
        //assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        //assertEquals(null, activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(taskInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());
        assertNotNull(taskInfo.getConfigurations());
        assertEquals(1, taskInfo.getConfigurations().size());
        assertEquals("PROD", taskInfo.getConfigurations().get("stage"));
    }

    @Test
    public void lifecycleDpdEmptyTest() {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_LIFECYCLE_EMPTY.getContentLocation();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location", t);
        }

        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");

        ParseResult result = null;

        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }

        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        List<LifecycleTaskInfoDPDS> tasksInfo = null;
        LifecycleTaskInfoDPDS taskInfo = null;
        ObjectNode templateDefinitionNode = null;

        assertTrue(descriptor != null);
        assertNotNull(descriptor.getInternalComponents());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo());
        assertEquals(4, descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo().size());

        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("dev");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getService());
        assertEquals(null, taskInfo.getTemplate());
        assertEquals(null, taskInfo.getConfigurations());

        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("qa");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getService());
        assertNotNull(taskInfo.getTemplate());
        assertEquals(null, taskInfo.getConfigurations());

        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("prod");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getService());
        assertNotNull(taskInfo.getTemplate());
        assertNotNull(taskInfo.getConfigurations());

        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("deprecated");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertEquals(null, taskInfo.getService());
        assertEquals(null, taskInfo.getTemplate());
        assertEquals(null, taskInfo.getConfigurations());

    }

    @Test
    public void lifecycleDpdExternalRefTest() {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_LIFECYCLE_EREF.getUriLocation();
        } catch (Exception e) {
            fail("Impossible to get descriptor location", e);
        }

        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");

        ParseResult result = null;

        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }

        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        List<LifecycleTaskInfoDPDS> tasksInfo = null;
        LifecycleTaskInfoDPDS taskInfo = null;
        ObjectNode templateDefinitionNode = null;

        assertTrue(descriptor != null);
        assertNotNull(descriptor.getInternalComponents());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo());
        assertEquals(4, descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo().size());

        //DEV
        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("dev");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getTemplate());
        assertEquals("spec", taskInfo.getTemplate().getSpecification());
        assertEquals("2.0", taskInfo.getTemplate().getSpecificationVersion());
        assertNotNull(taskInfo.getTemplate().getDefinition());
        assertEquals(null, taskInfo.getTemplate().getDefinition().getDescription());
        //assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        //assertEquals("template.json", activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(taskInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // QA
        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("qa");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getTemplate());
        assertNotNull(taskInfo.getTemplate());
        assertEquals("spec", taskInfo.getTemplate().getSpecification());
        assertEquals("2.0", taskInfo.getTemplate().getSpecificationVersion());
        assertNotNull(taskInfo.getTemplate().getDefinition());
        assertEquals(null, taskInfo.getTemplate().getDefinition().getDescription());
        //assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        //assertEquals("template.json", activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(taskInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // PROD
        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("prod");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getTemplate());
        assertNotNull(taskInfo.getTemplate());
        assertEquals("spec", taskInfo.getTemplate().getSpecification());
        assertEquals("2.0", taskInfo.getTemplate().getSpecificationVersion());
        assertNotNull(taskInfo.getTemplate().getDefinition());
        assertEquals(null, taskInfo.getTemplate().getDefinition().getDescription());
        //assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        //assertEquals("template.json", activityInfo.getTemplate().getDefinition().getOriginalRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(taskInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // DEPRECATED
        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("deprecated");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertEquals(null, taskInfo.getService());
        assertEquals(null, taskInfo.getTemplate());
        assertEquals(null, taskInfo.getConfigurations());
    }

    @Test
    public void lifecycleDpdInternalRefTest() {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_LIFECYCLE_IREF.getUriLocation();
        } catch (Exception e) {
            fail("Impossible to get descriptor location", e);
        }

        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");

        ParseResult result = null;

        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }

        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        List<LifecycleTaskInfoDPDS> tasksInfo = null;
        LifecycleTaskInfoDPDS taskInfo = null;
        ObjectNode templateDefinitionNode = null;

        assertTrue(descriptor != null);
        assertNotNull(descriptor.getInternalComponents());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo());
        assertNotNull(descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo());
        assertEquals(4, descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo().size());

        //DEV
        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("dev");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getTemplate());
        assertEquals("spec", taskInfo.getTemplate().getSpecification());
        assertEquals("2.0", taskInfo.getTemplate().getSpecificationVersion());
        assertNotNull(taskInfo.getTemplate().getDefinition());
        assertEquals(null, taskInfo.getTemplate().getDefinition().getDescription());
        //assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());

        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(taskInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // QA
        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("qa");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getTemplate());
        assertNotNull(taskInfo.getTemplate());
        assertEquals("spec", taskInfo.getTemplate().getSpecification());
        assertEquals("2.0", taskInfo.getTemplate().getSpecificationVersion());
        assertNotNull(taskInfo.getTemplate().getDefinition());
        assertEquals(null, taskInfo.getTemplate().getDefinition().getDescription());
        //assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(taskInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());

        // PROD
        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("prod");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertNotNull(taskInfo.getTemplate());
        assertNotNull(taskInfo.getTemplate());
        assertEquals("spec", taskInfo.getTemplate().getSpecification());
        assertEquals("2.0", taskInfo.getTemplate().getSpecificationVersion());
        assertNotNull(taskInfo.getTemplate().getDefinition());
        assertEquals(null, taskInfo.getTemplate().getDefinition().getDescription());
        //assertEquals("application/json", activityInfo.getTemplate().getDefinition().getMediaType());
        //assertEquals("http://localhost:80/templates/{templateId}", activityInfo.getTemplate().getDefinition().getRef());
        
        try {
            templateDefinitionNode = (ObjectNode) ObjectMapperFactory.JSON_MAPPER
                    .readTree(taskInfo.getTemplate().getDefinition().getRawContent());
        } catch (JsonProcessingException e) {
            fail("Impossible to parse template definition", e);
        }
        assertEquals("dpdLifecyclePipe", templateDefinitionNode.get("pipeline").asText());
        assertEquals("1.0.0", templateDefinitionNode.get("version").asText());



        tasksInfo = descriptor.getInternalComponents().getLifecycleInfo().getTasksInfo("deprecated");
        assertNotNull(tasksInfo);
        taskInfo = tasksInfo.get(0);
        assertNotNull(taskInfo);
        assertEquals(null, taskInfo.getService());
        assertEquals(null, taskInfo.getTemplate());
        assertEquals(null, taskInfo.getConfigurations());
    }

}
