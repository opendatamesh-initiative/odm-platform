package org.opendatamesh.platform.core.dpds;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;

public enum DPDSTestResources {

    DPD_MINIMAL("dpd/dpd-minimal.json"),
    DPD_CORE("dpd/dpd-core.json"),
    DPD_CORE_PROPS_READONLY("dpd/dpd-core-props-readonly.json"),
    DPD_CORE_PROPS_READONLY_WRONG("dpd/dpd-core-props-readonly-wrong.json"),
    DPD_CORE_PROPS_CUSTOM("dpd/dpd-core-props-custom.json"),
    DPD_CORE_WITH_EXTERNAL_REF("dpd-references/dpd-core-external/dpd-core.json"),


    DPD_FULL("dpd/dpd-full.json"),

    DPD_LIFECYCLE("dpd-lifecycle/dpd-lifecycle.json"),
    DPD_LIFECYCLE_EMPTY("dpd-lifecycle/dpd-lifecycle-empty.json"),
    DPD_LIFECYCLE_EREF("dpd-lifecycle/dpd-lifecycle-eref.json"),
    DPD_LIFECYCLE_IREF("dpd-lifecycle/dpd-lifecycle-iref.json");

    public String path;

    private DPDSTestResources(String path) {
        this.path = path;
    }

    public Path getPath() {
        ClassLoader cl = getClass().getClassLoader();
        String absoluteFilePath = cl.getResource(path).getFile();
        return Path.of(absoluteFilePath);
    }

    public URI getUri() throws IOException {
        URI absoluteFileUri = null;

        ClassLoader cl = getClass().getClassLoader();
        try {
            absoluteFileUri = cl.getResource(path).toURI();
        } catch (Throwable t) {
            throw new IOException("Impossible to get URI of resorurce", t);
        }

        return absoluteFileUri;
    }

    public String getContent() throws IOException {
        return  Files.readString(getPath());
    }

    public DescriptorLocation getUriLocation() throws IOException {
        return new UriLocation(getUri());
    }

    public DescriptorLocation getContentLocation() throws IOException {
        return new UriLocation(getContent());
    }

    
}
