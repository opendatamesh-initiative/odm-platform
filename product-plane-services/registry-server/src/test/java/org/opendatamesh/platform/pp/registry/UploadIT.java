package org.opendatamesh.platform.pp.registry;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.opendatamesh.platform.core.dpds.exceptions.BuildException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfoDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductDescriptorLocationResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

@TestPropertySource(properties = { "spring.test.context.parallel.enabled=false" })
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Disabled
public class UploadIT extends OpenDataMeshIT {

    // ----------------------------------------
    // CREATE Data product version
    // ----------------------------------------
    @Test
    @Order(1)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionUriUpload() throws IOException {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(RESOURCE_DPS_URI);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        try {
            verifyBasicContent(descriptorContent);
        } catch (BuildException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionGitUpload() throws IOException {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri("examples/tripexecution/data-product-descriptor.json");
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri("git@github.com:opendatamesh-initiative/odm-specification-dpdescriptor.git");
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        
        try {
            verifyBasicContent(descriptorContent);
        } catch (BuildException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @Disabled
    @Order(3)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionDevOpsUploadMain() throws IOException {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri("data-product-descriptor.json");
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri("git@ssh.dev.azure.com:v3/andreagioia/opendatamesh/odm-dpds-examples");
        git.setBranch(null);
        git.setTag(null);
        descriptorLocation.setGit(git);
        
        String descriptorContent = uploadDataProductVersion(descriptorLocation);

        DataProductVersionDPDS dpv = null;
        try {
            dpv = toDescriptor(descriptorContent);
            Assert.assertEquals(dpv.getInfo().getVersionNumber(), "1.1.0");
        } catch (BuildException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @Disabled
    @Order(4)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionDevOpsUploadTag() throws IOException {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri("data-product-descriptor.json");
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri("git@ssh.dev.azure.com:v3/andreagioia/opendatamesh/odm-dpds-examples");
        git.setBranch(null);
        git.setTag("v1.0.0");
        descriptorLocation.setGit(git);
        
        String descriptorContent = uploadDataProductVersion(descriptorLocation);

        DataProductVersionDPDS dpv = null;
        try {
            dpv = toDescriptor(descriptorContent);
            Assert.assertEquals(dpv.getInfo().getVersionNumber(), "1.0.0");
        } catch (BuildException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @Disabled
    @Order(5)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionDevOpsUploadBranch() throws IOException {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri("data-product-descriptor.json");
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri("git@ssh.dev.azure.com:v3/andreagioia/opendatamesh/odm-dpds-examples");
        git.setBranch("dev");
        git.setTag(null);
        descriptorLocation.setGit(git);
        
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        DataProductVersionDPDS dpv = null;
        try {
            dpv = toDescriptor(descriptorContent);
            Assert.assertEquals(dpv.getInfo().getVersionNumber(), "2.0.0");
        } catch (BuildException e) {
            Assert.fail(e.getMessage());
        }
        /* 
        descriptorLocation.getGit().setBranch(null);
        descriptorLocation.getGit().setTag("v1.0.0");
        descriptorContent = uploadDataProductVersion(descriptorLocation);
        dpv = null;
        try {
            dpv = toDescriptor(descriptorContent);
            Assert.assertEquals(dpv.getInfo().getVersionNumber(), "1.0.0");
        } catch (BuildException e) {
            Assert.fail(e.getMessage());
        }
        */
    }


    // ======================================================================================
    // PRIVATE METHODS
    // ======================================================================================

    // ----------------------------------------
    // Create test resources
    // ----------------------------------------

    // TODO ...as needed

    // ----------------------------------------
    // Verify test resources
    // ----------------------------------------

    private DataProductVersionDPDS toDescriptor(String descriptorContent) throws BuildException {
        DPDSParser parser = new DPDSParser();
        DescriptorLocation location = new UriLocation(descriptorContent);

        ParseOptions options = new ParseOptions();
        options.setResoveExternalRef(false);
        options.setResoveInternalRef(false);
        options.setResoveReadOnlyProperties(false);
        options.setResoveApiDefinitions(false);
        options.setResoveTemplateDefinitions(false);
        DataProductVersionDPDS dpv = parser.parse(location, options).getDescriptorDocument();
        return dpv;
    }
    private void verifyBasicContent(String descriptorContent) throws BuildException {
        DataProductVersionDPDS dpv = toDescriptor(descriptorContent);
    
        Assert.assertEquals(dpv.getDataProductDescriptor(), "1.0.0");

        InfoDPDS info = dpv.getInfo();
        Assert.assertNotNull(info);
        Assert.assertEquals(info.getName(), "tripExecution");
        Assert.assertEquals(info.getVersionNumber(), "1.2.3"); //FIXME!
        Assert.assertEquals(info.getDomain(), "Transport Management");
        Assert.assertEquals(info.getFullyQualifiedName(), "urn:org.opendatamesh:dataproduct:tripExecution");
        //Assert.assertEquals(info.getEntityType(), "dataproduct");
        //Assert.assertEquals(info.getDataProductId(), "3187c1fa-dd44-344a-a5b5-a4e86677b5dd"); //FIXME!
   
        List<PortDPDS> ports = dpv.getInterfaceComponents().getInputPorts();
        Assert.assertNotNull(ports);
        Assert.assertEquals(ports.size(), 1);

        ports = dpv.getInterfaceComponents().getOutputPorts();
        Assert.assertNotNull(ports);
        Assert.assertEquals(ports.size(), 2);
    
        PortDPDS port = ports.get(0);
        Assert.assertEquals(port.getFullyQualifiedName(), "urn:org.opendatamesh:dataproduct:tripExecution:1.2.3:outputport:tripStatus:1.2.0");
        Assert.assertNotNull( port.getPromises().getApi().getDefinition().getRef() ); // FIXME!
        //Assert.assertEquals(port.getEntityType(), "outputport");
        //Assert.assertEquals(port.getId(), "3497405b-7034-3989-98d5-c67318f05806"); 
   
        port = ports.get(1);
        Assert.assertEquals(port.getFullyQualifiedName(), "urn:org.opendatamesh:dataproduct:tripExecution:1.2.3:outputport:tripEvents:1.2.0");
        Assert.assertNotNull( port.getPromises().getApi().getDefinition().getRef() ) ; // FIXME!
        //Assert.assertEquals(port.getEntityType(), "outputport");
        //Assert.assertEquals(port.getId(), "62b02846-2cf0-35fa-95b5-7cf24402748b"); 

        ports = dpv.getInterfaceComponents().getObservabilityPorts();
        Assert.assertNotNull(ports);
        Assert.assertEquals(ports.size(), 1);
        
        port = ports.get(0);
        Assert.assertEquals(port.getFullyQualifiedName(), "urn:org.opendatamesh:dataproduct:tripExecution:1.2.3:observabilityport:helthMetrics:1.2.0");
        Assert.assertNotNull( port.getPromises().getApi().getDefinition().getRef() ); // FIXME!
        //Assert.assertEquals(port.getEntityType(), "observabilityport");
        //Assert.assertEquals(port.getId(), "c96180ea-90bb-31b7-b6e6-0ebc0cebadcc"); 
    }
}
