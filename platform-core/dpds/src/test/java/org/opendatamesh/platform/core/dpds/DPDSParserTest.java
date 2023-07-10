package org.opendatamesh.platform.core.dpds;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.exceptions.BuildException;
import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;

public class DPDSParserTest {
    
    @Test
    public void parseTest() throws IOException, BuildException {
        String ROOT_DOC_LOACAL_FILEPATH = "descriptor.json";
        ClassLoader cl = getClass().getClassLoader();
        String filePath = cl.getResource(ROOT_DOC_LOACAL_FILEPATH).getFile();
        String descriptorContent = Files.readString(Path.of(filePath));
        DescriptorLocation location = new UriLocation(descriptorContent);
        DPDSParser parser = new DPDSParser();
        ParseOptions options = new ParseOptions();
        options.setServerUrl( "http://localhost:80/");
        options.setResoveTemplateDefinitions(false);

        ParseResult result = parser.parse(location, options);
        DataProductVersionDPDS descriptor = result.getDescriptorDocument();

        assertTrue(descriptor != null);
        assertTrue(descriptor.getInternalComponents() != null);
        assertTrue(descriptor.getInternalComponents().getInfrastructuralComponents() != null);



        System.out.println(descriptorContent);

        List<ApplicationComponentDPDS> appComponents = descriptor.getInternalComponents().getApplicationComponents();
        assertTrue(appComponents.size() == 1);
        ApplicationComponentDPDS appComponent = appComponents.get(0);
        assertTrue(appComponent.getBuildInfo() != null);
        assertTrue(appComponent.getDeployInfo() != null);

        List<InfrastructuralComponentDPDS> infraComponents = descriptor.getInternalComponents().getInfrastructuralComponents();
        assertTrue(infraComponents.size() == 1);
        InfrastructuralComponentDPDS infraComponent = infraComponents.get(0);
        assertTrue(infraComponent.getProvisionInfo() != null);

    }
}
