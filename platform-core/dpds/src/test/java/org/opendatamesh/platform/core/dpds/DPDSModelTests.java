package org.opendatamesh.platform.core.dpds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.model.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DPDSModelTests extends DPDSTests {

    @Test
    public void portsEqualToTest()  {
        PortDPDS port1 = new PortDPDS();
        port1.setName("Port Name");
        port1.setVersion("1.0.0");
        port1.setRawContent("port 1 raw content");

        PortDPDS port2 = new PortDPDS();
        port2.setName("Port Name");
        port2.setVersion("1.0.0");
        port2.setRawContent("port 2 raw content");

        assertThat(port1).isEqualTo(port2);
    }

    @Test
    public void referencesEqualToTest()  {
        ReferenceObjectDPDS defRef1 = new DefinitionReferenceDPDS();
        defRef1.setRef("RefA");
        defRef1.setRawContent("def 1 raw content");
        
        ReferenceObjectDPDS defRef2 = new DefinitionReferenceDPDS();
        defRef2.setRef("RefA");
        defRef2.setRawContent("def 2 raw content");

        assertThat(defRef1).isEqualTo(defRef2);
    }

    @Test
    public void definitionsReferencesEqualToTest()  {
        DefinitionReferenceDPDS defRef1 = new DefinitionReferenceDPDS();
        defRef1.setRef("RefA");
        defRef1.setRawContent("def 1 raw content");
        
        DefinitionReferenceDPDS defRef2 = new DefinitionReferenceDPDS();
        defRef2.setRef("RefA");
        defRef2.setRawContent("def 2 raw content");

        assertThat(defRef1).isEqualTo(defRef2);
    }

    @Test
    public void standardDefinitionsEqualToTest()  {

        DefinitionReferenceDPDS defRef1 = new DefinitionReferenceDPDS();
        defRef1.setRef("RefA");
        defRef1.setRawContent("def 1 raw content");
        
        DefinitionReferenceDPDS defRef2 = new DefinitionReferenceDPDS();
        defRef2.setRef("RefA");
        defRef2.setRawContent("def 2 raw content");

        assertThat(defRef1).isEqualTo(defRef2);

        StandardDefinitionDPDS stdDef1 = new StandardDefinitionDPDS();
        stdDef1.setName("Std def A");
        stdDef1.setDefinition(defRef1);
        
        StandardDefinitionDPDS stdDef2 = new StandardDefinitionDPDS();
        stdDef2.setName("Std def A");
        stdDef2.setDefinition(defRef2);

        assertThat(stdDef1).isEqualTo(stdDef2);
    }

    @Test
    public void interfaceComponentsEmptyPropsTest() throws JsonProcessingException  {

        InterfaceComponentsDPDS interfaces = new InterfaceComponentsDPDS();
        PortDPDS port1 = new PortDPDS();
        port1.setName("Port Name");
        port1.setVersion("1.0.0");
        port1.setRawContent("port 1 raw content");
        interfaces.getControlPorts().add(port1);

        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        
        ObjectNode interfacesNode = null;
        try {
            String content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(interfaces);
            System.out.println(content);
            interfacesNode = (ObjectNode) mapper.readTree(content);
        } catch (Throwable t) {
            fail("Impossible to parse node", t);
        }

        assertThat(interfacesNode.at("/inputPorts").isMissingNode()).isTrue();
        assertThat(interfacesNode.at("/outputPorts").isMissingNode()).isFalse();
        assertThat(interfacesNode.at("/controlPorts").isMissingNode()).isFalse();
        assertThat(interfacesNode.at("/discoveryport").isMissingNode()).isTrue();
        assertThat(interfacesNode.at("/observabilityport").isMissingNode()).isTrue();
    }
    
}
