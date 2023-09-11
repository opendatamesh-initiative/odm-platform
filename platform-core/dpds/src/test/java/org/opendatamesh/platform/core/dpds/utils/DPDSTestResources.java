package org.opendatamesh.platform.core.dpds.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;

public enum DPDSTestResources {

    DPD_MINIMAL("dpd/dpd-minimal.json"),
    DPD_CORE("dpd/dpd-core.json"),
    DPD_CORE_PROPS_READONLY("dpd/dpd-core-props-readonly.json"),
    DPD_CORE_PROPS_READONLY_WRONG("dpd/dpd-core-props-readonly-wrong.json"),
    DPD_CORE_PROPS_CUSTOM("dpd/dpd-core-props-custom.json"),
    DPD_CORE_WITH_EXTERNAL_REF("dpd-references/dpd-core-external/dpd-core.json"),
    DPD_CORE_WITH_INTERNAL_REF("dpd-references/dpd-core-internal/dpd-core.json"),
    DPD_CORE_WITH_MIX_REF("dpd-references/dpd-core-mix/dpd-core.json"),

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

        /* 
        String content = "";

       try {
            Path p = Path.of(path);
            if (Files.exists(p)) {
                content = FileUtils.readFileToString(p.toFile(), StandardCharsets.UTF_8.displayName());
            } else {
                content = loadFileFromClasspath(path);
            }
        } catch (IOException e) {
            throw new IOException("Impossible to get resource [" + path + "] content", e);
        }

        return content;
        */
    }

    private String loadFileFromClasspath(String location) throws IOException {

        String content = "";

        String file = FilenameUtils.separatorsToUnix(location);

        InputStream inputStream = DPDSTestResources.class.getResourceAsStream(file);

        if (inputStream == null) {
            inputStream = DPDSTestResources.class.getClassLoader().getResourceAsStream(file);
        }

        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(file);
        }

        if (inputStream != null) {
            try {
                content = IOUtils.toString(inputStream, Charset.forName(StandardCharsets.UTF_8.displayName()));
            } catch (IOException e) {
                throw new RuntimeException("Could not read " + file + " from the classpath", e);
            }
        } else {
            throw new IOException("Impossible to get resource [" + path + "] content from classpath");
        }

        return content;
    }

    public DescriptorLocation getUriLocation() throws IOException {
        return new UriLocation(getUri());
    }

    public DescriptorLocation getContentLocation() throws IOException {
        return new UriLocation(getContent());
    }
}
