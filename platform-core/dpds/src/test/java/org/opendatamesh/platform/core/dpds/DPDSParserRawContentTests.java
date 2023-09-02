package org.opendatamesh.platform.core.dpds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import static  org.opendatamesh.platform.core.dpds.DescriptorCoreChecker.verifyAll;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DPDSParserRawContentTests extends DPDSTests {

    @Test
    public void parseDpdCoreRawContentCustomPropsTest()  {

        ParseResult result = parseDescriptorFromContent(DPDSTestResources.DPD_CORE_PROPS_CUSTOM, null);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
        verifyAll(descriptor);

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
        
        rawContent = lifecycle.getActivityInfo("test").getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse activity raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");

        
        rawContent = lifecycle.getActivityInfo("prod").getRawContent();
        assertThat(rawContent).isNotNull();
        try {
            parsedRawContentNode = (ObjectNode)mapper.readTree(rawContent);
        } catch(Throwable t) {
            fail("Impossible to parse activity raw content", t);
        }
        assertThat(parsedRawContentNode.get("x-prop")).isNotNull();
        assertThat(parsedRawContentNode.get("x-prop").asText()).isEqualTo("x-prop-value");

        rawContent = lifecycle.getActivityInfo("prod").getRawContent();
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
