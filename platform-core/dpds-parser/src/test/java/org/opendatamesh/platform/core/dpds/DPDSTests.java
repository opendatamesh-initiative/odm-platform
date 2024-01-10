package org.opendatamesh.platform.core.dpds;

import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSParser;
import org.opendatamesh.platform.core.dpds.parser.DPDSSerializer;
import org.opendatamesh.platform.core.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.dpds.parser.ParseResult;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.utils.DPDSTestResources;

import static org.assertj.core.api.Assertions.fail;

public class DPDSTests {

    public static final ParseOptions DEFAULT_PARSE_OPTION;

    static {
        DEFAULT_PARSE_OPTION = new ParseOptions();
        DEFAULT_PARSE_OPTION.setServerUrl("http://localhost:80");
    }

    protected ParseResult parseDescriptorFromContent(DPDSTestResources resource, ParseOptions options) {
        DescriptorLocation location = getContentLocation(resource);
        return parseDescriptor(location, options);
    }

    protected ParseResult parseDescriptorFromUri(DPDSTestResources resource, ParseOptions options) {
        DescriptorLocation location = getUriLocation(resource);
        return parseDescriptor(location, options);
    }

    protected ParseResult parseDescriptorFromGit(DPDSTestResources resource, ParseOptions options) {
        DescriptorLocation location = getGitLocation(resource);
        return parseDescriptor(location, options);
    }

    protected ParseResult parseDescriptor(DescriptorLocation location, ParseOptions options) {

        ParseResult result = null;

        if (options == null) {
            options = DEFAULT_PARSE_OPTION;
        }

        DPDSParser parser = new DPDSParser();
        try {
            result = parser.parse(location, options);
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Impossible to parse descriptor", e);
        }

        return result;
    }

    protected DescriptorLocation getContentLocation(DPDSTestResources resource) {
        DescriptorLocation location = null;
        try {
            location = resource.getContentLocation();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location fomp path [" + resource.localPath + "]", t);
        }

        return location;
    }

    protected DescriptorLocation getUriLocation(DPDSTestResources resource) {
        DescriptorLocation location = null;
        try {
            location = resource.getUriLocation();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location fomp path [" + resource.localPath + "]", t);
        }

        return location;
    }

    protected DescriptorLocation getGitLocation(DPDSTestResources resource) {
        DescriptorLocation location = null;
        try {
            location = resource.getGitLocation();
        } catch (Throwable t) {
            fail("Impossible to get descriptor location fomp git [" + resource.repoPath + "]", t);
        }

        return location;
    }

    protected String serializeDescriptor(DataProductVersionDPDS descriptor, String form, String mediaType) {
        String descriptorContent = null;

        DPDSSerializer serializer = new DPDSSerializer(mediaType, true);
        try {
            descriptorContent = serializer.serialize(descriptor, form);
        } catch (Throwable t) {
            fail("Impossible to serialize descriptor", t);
        }

        return descriptorContent;
    }

    // ==========================================================================
    // Verify descriptor
    // ==========================================================================
}
