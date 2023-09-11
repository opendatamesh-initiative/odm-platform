package org.opendatamesh.platform.pp.registry.server.utils;

import java.io.IOException;
import java.net.URI;

public enum ODMRegistryResources {
    
    DPD_MINIMAL("dpd/dpd-minimal.json"),
    DPD_CORE("dpd/dpd-core.json"),
    DPD_CORE_PROPS_READONLY("dpd/dpd-core-props-readonly.json"),
    DPD_CORE_PROPS_READONLY_WRONG("dpd/dpd-core-props-readonly-wrong.json"),
    DPD_CORE_PROPS_CUSTOM("dpd/dpd-core-props-custom.json"),
    DPD_CORE_WITH_EXTERNAL_REF("dpd-references/dpd-core-external/dpd-core.json"),
    DPD_CORE_WITH_INTERNAL_REF("dpd-references/dpd-core-internal/dpd-core.json"),
    DPD_CORE_WITH_MIX_REF("dpd-references/dpd-core-mix/dpd-core.json"),

     
    RESOURCE_SCHEMA1("test/schema/schema1.json"),
    
    RESOURCE_DPS_URI("https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json");

    public String path;

    private ODMRegistryResources(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
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
}
