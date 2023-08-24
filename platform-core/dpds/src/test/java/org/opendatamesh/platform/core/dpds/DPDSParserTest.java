package org.opendatamesh.platform.core.dpds;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.exceptions.BuildException;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DPDSParserTest {

    
    String DPD_MINIMAL = "dpd-minimal.json";
    String DPD_FULL = "descriptor.json";
    
    private DescriptorLocation getLocation(String filePath) throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        String absoluteFilePath = cl.getResource(filePath).getFile();
        String descriptorContent = Files.readString(Path.of(absoluteFilePath));
        DescriptorLocation location = new UriLocation(descriptorContent);
        return location;
    }
    
     @Test
    public void minimalDpdTest() {
        
        DescriptorLocation location = null;
        try {
            location = getLocation(DPD_MINIMAL);
        } catch (IOException e) {
           fail("Impossible to get descriptor location", e);
        }

        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl( "http://localhost:80/");
       

        ParseResult result = null;
        
        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor", e);
        }
        
        
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        assertTrue(descriptor != null);
        assertEquals("1.0.0", descriptor.getDataProductDescriptor());
        assertNotNull(descriptor.getInfo());
        assertEquals("urn:org.opendatamesh:dataproducts:dpdMinimal", descriptor.getInfo().getFullyQualifiedName());
        assertEquals("dpdMinimal", descriptor.getInfo().getName());
        assertEquals("1.0.0", descriptor.getInfo().getVersionNumber());
        assertEquals("testDomain", descriptor.getInfo().getDomain());
        assertNotNull(descriptor.getInfo().getOwner());
        assertNotNull("john.doe@company-xyz.com", descriptor.getInfo().getOwner().getId());
        assertTrue(descriptor.getInfo().getOwner().getName() == null);
        assertNotNull(descriptor.getInterfaceComponents());
        assertNotNull(descriptor.getInterfaceComponents().getOutputPorts());
        assertEquals(0, descriptor.getInterfaceComponents().getOutputPorts().size());
        assertTrue(descriptor.getInternalComponents() == null);
    }

  
    @Test
    public void fullDpdTest() throws IOException, BuildException {
        
        DescriptorLocation location = getLocation(DPD_FULL);
        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl( "http://localhost:80/");
        options.setResoveTemplateDefinitions(false);

        ParseResult result = parser.parse(location, options);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        assertTrue(descriptor != null);
        assertTrue(descriptor.getInternalComponents() != null);
        assertTrue(descriptor.getInternalComponents().getInfrastructuralComponents() != null);

        List<ApplicationComponentDPDS> appComponents = descriptor.getInternalComponents().getApplicationComponents();
        assertTrue(appComponents.size() == 1);
        ApplicationComponentDPDS appComponent = appComponents.get(0);
        // TODO

        List<InfrastructuralComponentDPDS> infraComponents = descriptor.getInternalComponents().getInfrastructuralComponents();
        assertTrue(infraComponents.size() == 1);
        InfrastructuralComponentDPDS infraComponent = infraComponents.get(0);
        // TODO

    }
}
