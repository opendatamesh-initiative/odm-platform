package org.opendatamesh.platform.core.dpds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSSerializer;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.core.dpds.utils.DPDSTestResources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DPDSSerializerTests extends DPDSTests {
   
    @Test
    public void serializeEmptyFieldTest() {
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80");
        options.setValidate(false);
        
        ParseResult result = parseDescriptorFromContent(DPDSTestResources.DPD_MINIMAL, options);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
        

        DPDSSerializer serializer = new DPDSSerializer();
        String descriptorContent = null;
        try {
            descriptorContent = DPDSSerializer.DEFAULT_JSON_SERIALIZER.serialize(descriptor, "canonical");
        } catch (Throwable t) {
            fail("Impossible to serialize descriptor", t);
        }
        System.out.println(descriptorContent);

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

        ObjectNode descriptorNode = null;
        try {
            descriptorNode = (ObjectNode)mapper.readTree(descriptorContent);
        } catch(Throwable t) {
            fail("Impossible to parse root entity raw content", t);
        }

        assertThat(descriptorNode.at("/interfaceComponents/inputPorts").isMissingNode()).isTrue();
        assertThat(descriptorNode.at("/interfaceComponents/outputPorts").isMissingNode()).isFalse();
        assertThat(descriptorNode.at("/interfaceComponents/controlPorts").isMissingNode()).isTrue();
        assertThat(descriptorNode.at("/interfaceComponents/discoveryport").isMissingNode()).isTrue();
        assertThat(descriptorNode.at("/interfaceComponents/observabilityport").isMissingNode()).isTrue();
    }


    @Test
    public void parseDpdMinimalCanonicalTest() {

        ParseResult result = parseDescriptorFromContent(DPDSTestResources.DPD_MINIMAL, null);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
        

        DPDSSerializer serializer = new DPDSSerializer();
        String descriptorContent = null;
        try {
            descriptorContent = DPDSSerializer.DEFAULT_JSON_SERIALIZER.serialize(descriptor, "canonical");
        } catch (Throwable t) {
            fail("Impossible to serialize descriptor", t);
        }
        System.out.println(descriptorContent);

        // parse descriptor in canonical form
        result = parseDescriptor(new UriLocation(descriptorContent), null);
        descriptor = result.getDescriptorDocument();
        try {
            descriptorContent = DPDSSerializer.DEFAULT_JSON_SERIALIZER.serialize(descriptor, "canonical");
        } catch (Throwable t) {
            fail("Impossible to serialize descriptor", t);
        }
        System.out.println(descriptorContent);
    }

    @Test
    public void parseDpdCoreCanonicalTest() {

        ParseResult result = parseDescriptorFromContent(DPDSTestResources.DPD_CORE, null);
        DataProductVersionDPDS descriptorParsedFormSource = result.getDescriptorDocument();

        String descriptorContent = serializeDescriptor(descriptorParsedFormSource, "canonical", "json");
              
        result = parseDescriptor(new UriLocation(descriptorContent), null);
        DataProductVersionDPDS descriptorParsedFromCanonical = result.getDescriptorDocument();
        
        DPDSTestResources.DPD_CORE.getObjectChecker().verifyAll(descriptorParsedFromCanonical);
    }
}
