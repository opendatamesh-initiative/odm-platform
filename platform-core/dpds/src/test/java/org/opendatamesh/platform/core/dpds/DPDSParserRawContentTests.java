package org.opendatamesh.platform.core.dpds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleTaskInfoDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.utils.DPDSTestResources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DPDSParserRawContentTests extends DPDSTests {

    @Test
    public void parseDpdCoreRawContentCustomPropsTest()  {

        ParseResult result = parseDescriptorFromContent(DPDSTestResources.DPD_CORE, null);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
        DPDSTestResources.DPD_CORE.getObjectChecker().verifyAll(descriptor);

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        ObjectNode parsedRawContentNode = null;
        String rawContent = null;

        rawContent = descriptor.getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse root entity raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");

        PortDPDS inputPort = descriptor.getInterfaceComponents().getInputPorts().get(0);
        rawContent = inputPort.getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse input port raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");
        assertThat(parsedRawContentNode.at("/promises/x-prop")).isNotNull();
        assertThat(parsedRawContentNode.at("/promises/x-prop").asText()).isEqualTo("x-prop-value");
        StandardDefinitionDPDS api = descriptor.getInterfaceComponents().getInputPorts().get(0).getPromises().getApi();
        rawContent = api.getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse api raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");

        PortDPDS outputPort = descriptor.getInterfaceComponents().getOutputPorts().get(0);
        rawContent =  outputPort.getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse output port raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");
        assertThat(parsedRawContentNode.at("/promises/x-prop")).isNotNull();
        assertThat(parsedRawContentNode.at("/promises/x-prop").asText()).isEqualTo("x-prop-value");
        api = descriptor.getInterfaceComponents().getOutputPorts().get(0).getPromises().getApi();
        rawContent = api.getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse api raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");

        ApplicationComponentDPDS app = descriptor.getInternalComponents().getApplicationComponents().get(0);
        rawContent =  app.getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse app raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");

        InfrastructuralComponentDPDS infra = descriptor.getInternalComponents().getInfrastructuralComponents().get(0);
        rawContent =  infra.getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse infra raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");

        LifecycleInfoDPDS  lifecycle =  descriptor.getInternalComponents().getLifecycleInfo();
        
        List<LifecycleTaskInfoDPDS>  tasksInfo = null;
        
        tasksInfo = lifecycle.getTasksInfo("test");
        assertThat(tasksInfo).isNotNull();
        assertThat(tasksInfo).size().isEqualTo(1);
        rawContent = tasksInfo.get(0).getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse activity raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");

        
        tasksInfo = lifecycle.getTasksInfo("prod");
        assertThat(tasksInfo).isNotNull();
        assertThat(tasksInfo).size().isEqualTo(1);
        rawContent = tasksInfo.get(0).getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse activity raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");
    }
}
