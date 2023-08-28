package org.opendatamesh.platform.core.dpds;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfoDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.InternalComponentsDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;

public class DPDSParserTests extends DPDSTests {

    @Test
    public void parseDpdMinimalTest() {

        ParseResult result = this.parseDescriptorFromContent(DPDSTestResources.DPD_MINIMAL, null);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getDataProductDescriptor()).isEqualTo("1.0.0");
        
        // check info
        InfoDPDS info = descriptor.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getFullyQualifiedName()).isEqualTo("urn:org.opendatamesh:dataproducts:dpdMinimal");
        assertThat(info.getName()).isEqualTo("dpdMinimal");
        assertThat(info.getVersionNumber()).isEqualTo("1.0.0");
        assertThat(info.getDomain()).isEqualTo("testDomain");
        assertThat(info.getOwner());
        assertThat(info.getOwner().getId()).isEqualTo("john.doe@company-xyz.com");
        assertThat(info.getOwner().getName()).isNull();
        
        // check interface components
        InterfaceComponentsDPDS interfaces = descriptor.getInterfaceComponents();
        assertThat(interfaces).isNotNull();
        assertThat(interfaces.getOutputPorts()).isNotNull();
        assertThat(interfaces.getOutputPorts().size()).isEqualTo(0);

        // check internal components
        InternalComponentsDPDS internals = descriptor.getInternalComponents();
        assertThat(internals).isNull();;
    }

    @Test
    public void parseDpdCoreTest()  {

        ParseResult result = parseDescriptorFromContent(DPDSTestResources.DPD_CORE, null);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
       
        verifyAll(descriptor);
    }

    @Test
    public void parseDpdCorePropsReadonlyTest()  {
      
        ParseResult result = parseDescriptorFromContent(DPDSTestResources.DPD_CORE_PROPS_READONLY, null);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
       
        verifyAll(descriptor);
    }

    @Test
    @Disabled
    public void parseDpdCoreTestWithExternalRef()  {

        ParseResult result = parseDescriptorFromUri(DPDSTestResources.DPD_CORE_WITH_EXTERNAL_REF, null);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();
        
        verifyAll(descriptor);
    }



    @Test
    public void parseFullDpdTest() throws IOException, ParseException {

        DescriptorLocation location = null;
        try {
            location = DPDSTestResources.DPD_FULL.getContentLocation();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location fomp path [" + DPDSTestResources.DPD_FULL.path + "]", t);
        }
        
        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl("http://localhost:80/");
        

        ParseResult result = parser.parse(location, options);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        assertTrue(descriptor != null);
        assertTrue(descriptor.getInternalComponents() != null);
        assertTrue(descriptor.getInternalComponents().getInfrastructuralComponents() != null);

        List<ApplicationComponentDPDS> appComponents = descriptor.getInternalComponents().getApplicationComponents();
        assertTrue(appComponents.size() == 1);
        ApplicationComponentDPDS appComponent = appComponents.get(0);
        // TODO

        List<InfrastructuralComponentDPDS> infraComponents = descriptor.getInternalComponents()
                .getInfrastructuralComponents();
        assertTrue(infraComponents.size() == 1);
        InfrastructuralComponentDPDS infraComponent = infraComponents.get(0);
        // TODO
    }
}
