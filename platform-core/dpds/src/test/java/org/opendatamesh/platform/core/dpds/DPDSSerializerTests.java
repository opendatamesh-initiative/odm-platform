package org.opendatamesh.platform.core.dpds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSSerializer;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
            descriptorContent = serializer.serialize(descriptor, "canonical", "json", true);
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
            descriptorContent = serializer.serialize(descriptor, "canonical", "json", true);
        } catch (Throwable t) {
            fail("Impossible to serialize descriptor", t);
        }
        System.out.println(descriptorContent);

        // parse descriptor in canonical form
        result = parseDescriptor(new UriLocation(descriptorContent), null);
        descriptor = result.getDescriptorDocument();
        try {
            descriptorContent = serializer.serialize(descriptor, "canonical", "json", true);
        } catch (Throwable t) {
            fail("Impossible to serialize descriptor", t);
        }
        System.out.println(descriptorContent);
    }

    @Test
    public void parseDpdCoreCanonicalTest() {

        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80/");

        ParseResult result = parseDescriptorFromContent(DPDSTestResources.DPD_CORE, options);
        DataProductVersionDPDS descriptorParsedFormSource = result.getDescriptorDocument();


        String descriptorContent = serializeDescriptor(descriptorParsedFormSource, "canonical", "json");
        System.out.println(descriptorContent);
      
        result = parseDescriptor(new UriLocation(descriptorContent), options);
        DataProductVersionDPDS descriptorParsedFromCanonical = result.getDescriptorDocument();
        
        assertThat(descriptorParsedFromCanonical).isNotNull();
        assertThat(descriptorParsedFromCanonical.getDataProductDescriptor()).isEqualTo("1.0.0");

        verifyCoreInfo(descriptorParsedFromCanonical);
        verifyCoreInterfaces(descriptorParsedFromCanonical);
        verifyCoreApplicationComponents(descriptorParsedFromCanonical);
        verifyCoreInfrastructuralComponents(descriptorParsedFromCanonical);

        assertThat(descriptorParsedFormSource).isEqualTo(descriptorParsedFromCanonical);
    }
}