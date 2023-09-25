package org.opendatamesh.platform.core.dpds.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.GitLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriUtils;

import lombok.Data;

@Data
public class DPDSTestResources {

    public static final String TEST_REPO_SSH_URI = "git@github.com:opendatamesh-initiative/odm-demo.git";
    public static final String TEST_REPO_RAW_URI = "https://raw.githubusercontent.com/opendatamesh-initiative/odm-demo/main";

    public static final DPDSTestResources DPD_MINIMAL = new DPDSTestResources(
            "testcases/dpd/allinone/dpd-minimal.json");

    public static final DPDSTestResources DPD_CORE = new DPDSTestResources(
            "testcases/dpd/allinone/dpd-core.json");

    public static final DPDSTestResources DPD_FULL = new DPDSTestResources(
            "testcases/dpd/allinone/dpd-full.json");

    public static final DPDSTestResources DPD_CORE_PROPS_READONLY = new DPDSTestResources(
            "testcases/dpd/allinone/dpd-core-props-readonly.json");

    public static final DPDSTestResources DPD_CORE_PROPS_READONLY_WRONG = new DPDSTestResources(
            "testcases/dpd/allinone/dpd-core-props-readonly-wrong.json");

    public static final DPDSTestResources DPD_CORE_WITH_EXTERNAL_REF = new DPDSTestResources(
            "testcases/dpd/references/dpd-core-external/dpd-core.json");

    public static final DPDSTestResources DPD_CORE_WITH_INTERNAL_REF = new DPDSTestResources(
            "testcases/dpd/references/dpd-core-internal/dpd-core.json");

    public static final DPDSTestResources DPD_CORE_WITH_MIX_REF = new DPDSTestResources(
            "testcases/dpd/references/dpd-core-mix/dpd-core.json");

    public static final DPDSTestResources DPD_LIFECYCLE = new DPDSTestResources(
            "testcases/dpd/lifecycle/dpd-lifecycle.json");
    public static final DPDSTestResources DPD_LIFECYCLE_EMPTY = new DPDSTestResources(
            "testcases/dpd/lifecycle/dpd-lifecycle-empty.json");
    public static final DPDSTestResources DPD_LIFECYCLE_EREF = new DPDSTestResources(
            "testcases/dpd/lifecycle/dpd-lifecycle-eref.json");
    public static final DPDSTestResources DPD_LIFECYCLE_IREF = new DPDSTestResources(
            "testcases/dpd/lifecycle/dpd-lifecycle-iref.json");

    public static final Map<Object, ResourceContentChecker> CONTENT_CHECKERS;
    public static final Map<Object, ResourceObjectChecker> OBJECT_CHECKERS;

    static {
        CONTENT_CHECKERS = new HashMap<Object, ResourceContentChecker>();
        OBJECT_CHECKERS = new HashMap<Object, ResourceObjectChecker>();

        CONTENT_CHECKERS.put(DPD_CORE, new DPDCoreContentChecker());
        OBJECT_CHECKERS.put(DPD_CORE, new DPDCoreObjectChecker());
    }

    public String localPath;
    public String remotePath;
    public String repoPath;
    public String repo;

    private DPDSTestResources(
            String path) {

        this(
                path,
                TEST_REPO_RAW_URI + "/" + path,
                path,
                TEST_REPO_SSH_URI);
    }

    private DPDSTestResources(
            String localPath,
            String remotePath,
            String repoPath,
            String repo) {

        this.localPath = localPath;
        this.remotePath = remotePath;
        this.repoPath = repoPath;
        this.repo = repo;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getRemotePath() {
        return remotePath;
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

    public DescriptorLocation getContentLocation() throws IOException, URISyntaxException {
        UriLocation location = new UriLocation(getContent());
        URI resourceUri = resolveLocalPath().toUri();
        location.setRootDocumentBaseUri(UriUtils.getResourcePathUri(resourceUri));
        return location;
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

    public static ResourceContentChecker getContentChecker(DPDSTestResources resource) {
        ResourceContentChecker checker = null;

        Objects.requireNonNull(resource, "Parameter [resource], cannot be null");

        checker = CONTENT_CHECKERS.get(resource);
        if (checker == null) {
            throw new RuntimeException("Checker not available for resource [" + resource + "]");
        }

        return checker;
    }

    public static ResourceObjectChecker getObjectChecker(DPDSTestResources resource) {
        ResourceObjectChecker checker = null;

        Objects.requireNonNull(resource, "Parameter [resource], cannot be null");

        checker = OBJECT_CHECKERS.get(resource);
        if (checker == null) {
            throw new RuntimeException("Checker not available for resource [" + resource + "]");
        }

        return checker;
    }
}
