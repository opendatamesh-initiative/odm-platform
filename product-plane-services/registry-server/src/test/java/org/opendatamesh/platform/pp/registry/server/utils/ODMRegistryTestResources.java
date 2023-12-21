package org.opendatamesh.platform.pp.registry.server.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.GitLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;

import lombok.Data;

@Data
public class ODMRegistryTestResources {
    
    public static final String TEST_REPO_SSH_URI = "git@github.com:opendatamesh-initiative/odm-demo.git";
    public static final String TEST_REPO_RAW_URI = "https://raw.githubusercontent.com/opendatamesh-initiative/odm-demo/main";

    public static final ODMRegistryTestResources DPD_MINIMAL = new ODMRegistryTestResources(
            "testcases/dpd/allinone/dpd-minimal.json");

    public static final ODMRegistryTestResources DPD_CORE = new ODMRegistryTestResources(
            "testcases/dpd/allinone/dpd-core.json");

    public static final ODMRegistryTestResources DPD_FULL = new ODMRegistryTestResources(
            "testcases/dpd/allinone/dpd-full.json");

    public static final ODMRegistryTestResources DPD_CORE_PROPS_READONLY = new ODMRegistryTestResources(
            "testcases/dpd/allinone/dpd-core-props-readonly.json");

    public static final ODMRegistryTestResources DPD_CORE_PROPS_READONLY_WRONG = new ODMRegistryTestResources(
            "testcases/dpd/allinone/dpd-core-props-readonly-wrong.json");

    public static final ODMRegistryTestResources DPD_CORE_WITH_EXTERNAL_REF = new ODMRegistryTestResources(
            "testcases/dpd/references/dpd-core-external/dpd-core.json");

    public static final ODMRegistryTestResources DPD_CORE_WITH_INTERNAL_REF = new ODMRegistryTestResources(
            "testcases/dpd/references/dpd-core-internal/dpd-core.json");

    public static final ODMRegistryTestResources DPD_CORE_WITH_MIX_REF = new ODMRegistryTestResources(
            "testcases/dpd/references/dpd-core-mix/dpd-core.json");

    public static final ODMRegistryTestResources DPD_EXAMPLE_WITH_INTERNAL_REF_WITH_SCHEMA =
            new ODMRegistryTestResources(
                    "testcases/dpd/references/dpd-example-internal-with-schema/dp-demo-v1.0.0.json"
            );

    public static final ODMRegistryTestResources DPD_LIFECYCLE = new ODMRegistryTestResources(
            "testcases/dpd/lifecycle/dpd-lifecycle.json");
    public static final ODMRegistryTestResources DPD_LIFECYCLE_EMPTY = new ODMRegistryTestResources(
            "testcases/dpd/lifecycle/dpd-lifecycle-empty.json");
    public static final ODMRegistryTestResources DPD_LIFECYCLE_EREF = new ODMRegistryTestResources(
            "testcases/dpd/lifecycle/dpd-lifecycle-eref.json");
    public static final ODMRegistryTestResources DPD_LIFECYCLE_IREF = new ODMRegistryTestResources(
            "testcases/dpd/lifecycle/dpd-lifecycle-iref.json");

    public static final ODMRegistryTestResources RESOURCE_SCHEMA1 = new ODMRegistryTestResources(
        "testcases/schema/schema1.json");
    public static final ODMRegistryTestResources RESOURCE_DOMAIN1 = new ODMRegistryTestResources(
        "testcases/domain/domain1.json");

    public static final ODMRegistryTestResources  RESOURCE_DPS_URI = new ODMRegistryTestResources(
        null,
        "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json",
        "examples/tripexecution/data-product-descriptor.json",
        "git@github.com:opendatamesh-initiative/odm-specification-dpdescriptor.git"
    );


    public static final Map<Object, ResourceContentChecker> CONTENT_CHECKERS;
    public static final Map<Object, ResourceObjectChecker> OBJECT_CHECKERS;

    static {
        CONTENT_CHECKERS = new HashMap<Object, ResourceContentChecker>();
        OBJECT_CHECKERS = new HashMap<Object, ResourceObjectChecker>();

        CONTENT_CHECKERS.put(DPD_CORE, new DPDCoreContentChecker());
        OBJECT_CHECKERS.put(DPD_CORE, new DPDCoreObjectChecker());
        OBJECT_CHECKERS.put(DPD_EXAMPLE_WITH_INTERNAL_REF_WITH_SCHEMA, new DPDExampleObjectChecker());
    }

    public String localPath;
    public String remotePath;
    public String repoPath;
    public String repo;

    private ODMRegistryTestResources(
            String path) {

        this(
                path,
                TEST_REPO_RAW_URI + "/" + path,
                path,
                TEST_REPO_SSH_URI);
    }

    private ODMRegistryTestResources(
            String localPath,
            String remotePath,
            String repoPath,
            String repo) {

        this.localPath = localPath;
        this.remotePath = remotePath;
        this.repoPath = repoPath;
        this.repo = repo;
    }


    private Path resolveLocalPath() {
        ClassLoader cl = getClass().getClassLoader();
        String absoluteFilePath = cl.getResource(getLocalPath()).getFile();
        return Path.of(absoluteFilePath);
    }

    public URI getLocalUri() throws IOException {
        URI resourceUri = null;

        try {
            ClassLoader cl = getClass().getClassLoader();
            resourceUri = cl.getResource(localPath).toURI();
        } catch (Throwable t) {
            throw new IOException("Impossible to get URI of resorurce", t);
        }

        return resourceUri;
    }

    public URI getRemoteUri() throws IOException {
        URI resourceUri = null;

        try {
            resourceUri = new URI(remotePath);
        } catch (Throwable t) {
            throw new IOException("Impossible to get URI of resorurce", t);
        }

        return resourceUri;
    }

    public URI getRepoUri() throws IOException {
        URI resourceUri = null;

        try {
            resourceUri = new URI(repoPath);
        } catch (Throwable t) {
            throw new IOException("Impossible to get URI of resorurce", t);
        }

        return resourceUri;
    }

    public DescriptorLocation getUriLocation() throws IOException {
        URI resourceUri = getRemoteUri();
        return new UriLocation(resourceUri);
    }

    public String getContent() throws IOException {
        String fileContent = null;

        Objects.requireNonNull(this, "Parameter [resource] cannot be null");

        fileContent = Files.readString(resolveLocalPath());

        return fileContent;
    }

    public DescriptorLocation getContentLocation() throws IOException {
        return new UriLocation(getContent());
    }

    public GitLocation getGitLocation() {
         URI descriptorUri = null;
        try {
            descriptorUri = getRepoUri();
        } catch (Throwable t) {
            fail("Impossible to parse uri [" + repoPath + "]");
        }

        return new GitLocation(
            repo, 
            descriptorUri,
            null, 
            null
        );
    }

    public <T> T getObject(Class<T> resourceType) throws IOException {
        String fileContent = getContent();
        return ObjectMapperFactory.getRightMapper(fileContent).readValue(fileContent, resourceType);
    }

    public ResourceContentChecker getContentChecker() {
        return getContentChecker(this);
    }

    public ResourceObjectChecker getObjectChecker() {
        return getObjectChecker(this);
    }

    public static ResourceContentChecker getContentChecker(ODMRegistryTestResources resource) {
        ResourceContentChecker checker = null;

        Objects.requireNonNull(resource, "Parameter [resource], cannot be null");

        checker = CONTENT_CHECKERS.get(resource);
        if (checker == null) {
            throw new RuntimeException("Checker not available for resource [" + resource + "]");
        }

        return checker;
    }

    public static ResourceObjectChecker getObjectChecker(ODMRegistryTestResources resource) {
        ResourceObjectChecker checker = null;

        Objects.requireNonNull(resource, "Parameter [resource], cannot be null");

        checker = OBJECT_CHECKERS.get(resource);
        if (checker == null) {
            throw new RuntimeException("Checker not available for resource [" + resource + "]");
        }

        return checker;
    }
}
