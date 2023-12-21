package org.opendatamesh.platform.pp.registry.server;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.exceptions.ParseException;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductDescriptorLocationResource;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryResourceBuilder;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryTestResources;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class UploadIT extends ODMRegistryIT {

   
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpCoreVersionFromUri()  {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(ODMRegistryTestResources.DPD_CORE.getRemotePath());
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);
    }

    
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpCoreVersionFromPublicGit()  {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(ODMRegistryTestResources.DPD_CORE.getRepoPath());
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri(ODMRegistryTestResources.DPD_CORE.getRepo());
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);
    }
    
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpCoreWithExternalRefVersionFromPublicGit() {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(ODMRegistryTestResources.DPD_CORE_WITH_EXTERNAL_REF.getRepoPath());
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri(ODMRegistryTestResources.DPD_CORE_WITH_EXTERNAL_REF.getRepo());
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpCoreWithInternalRefVersionPublicGit() {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(ODMRegistryTestResources.DPD_CORE_WITH_INTERNAL_REF.getRepoPath());
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri(ODMRegistryTestResources.DPD_CORE_WITH_INTERNAL_REF.getRepo());
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpExampleWithInternalRefWithSchemaVersionPublicGit() {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(
                ODMRegistryTestResources.DPD_EXAMPLE_WITH_INTERNAL_REF_WITH_SCHEMA.getRepoPath()
        );
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri(ODMRegistryTestResources.DPD_EXAMPLE_WITH_INTERNAL_REF_WITH_SCHEMA.getRepo());
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        try {
            DataProductVersionDPDS dataProductVersionDPDS = mapper.readValue(
                    descriptorContent,
                    DataProductVersionDPDS.class
            );
            ODMRegistryTestResources.DPD_EXAMPLE_WITH_INTERNAL_REF_WITH_SCHEMA.getObjectChecker().verifyAll(
                    dataProductVersionDPDS
            );
        } catch (JsonProcessingException e) {
            fail("Impossible to serialize descriptor as Data Product Version");
        }
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpCoreWithMixRefVersionPublicGit() {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(ODMRegistryTestResources.DPD_CORE_WITH_MIX_REF.getRepoPath());
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri(ODMRegistryTestResources.DPD_CORE_WITH_MIX_REF.getRepo());
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpCoreVersionFromPublicGitBranch()  {

        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(ODMRegistryTestResources.DPD_CORE.getRepoPath());
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri(ODMRegistryTestResources.DPD_CORE.getRepo());
        git.setBranch("test");
        git.setTag(null);
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        //ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);

        DataProductVersionDPDS dpv = null;
        try {
            DPDSParser parser = new DPDSParser();
            DescriptorLocation location = new UriLocation(descriptorContent);
            ParseOptions options = new ParseOptions();
            options.setServerUrl("http://localhost");
            dpv = parser.parse(location, options).getDescriptorDocument();
            assertThat(dpv.getInfo().getVersionNumber()).isEqualTo("2.0.0");
        } catch (ParseException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpCoreVersionFromPublicGitTag() throws IOException {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(ODMRegistryTestResources.DPD_CORE.getRepoPath());
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri(ODMRegistryTestResources.DPD_CORE.getRepo());
        git.setBranch(null);
        git.setTag("v0.9.0");
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        //ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);

        DataProductVersionDPDS dpv = null;
        try {
            DPDSParser parser = new DPDSParser();
            DescriptorLocation location = new UriLocation(descriptorContent);
            ParseOptions options = new ParseOptions();
            options.setServerUrl("http://localhost");
            dpv = parser.parse(location, options).getDescriptorDocument();
            assertThat(dpv.getInfo().getVersionNumber()).isEqualTo("0.9.0");
        } catch (ParseException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    @Disabled
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUploadDpCoreVersionFromPrivateGit() throws IOException {
        DataProductDescriptorLocationResource descriptorLocation = new DataProductDescriptorLocationResource();
        descriptorLocation.setRootDocumentUri(ODMRegistryTestResources.DPD_CORE.getRepoPath());
        DataProductDescriptorLocationResource.Git git = new DataProductDescriptorLocationResource.Git();
        git.setRepositorySshUri("git@ssh.dev.azure.com:v3/andreagioia/opendatamesh/odm-demo");
        descriptorLocation.setGit(git);
        String descriptorContent = uploadDataProductVersion(descriptorLocation);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);
    }


   
}
