package org.opendatamesh.platform.core.dpds.parser.location;

import java.net.URI;

import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.opendatamesh.platform.core.dpds.parser.ParseLocation;

import lombok.Data;

@Data
public class UriLocation implements ParseLocation {

    private URI rootDocURI;
    private URI rootDocBaseURI;
    private String rootDocFileName;
    private String rootDocContent;

    public UriLocation(String descriptorContent) {
        rootDocContent = descriptorContent;
    }

    public UriLocation(URI descriptorUri) {
       setDescriptorUri(descriptorUri);
    }

    UriLocation() {

    }

    void setDescriptorUri(URI descriptorUri) {
        rootDocURI = descriptorUri.normalize();
        rootDocBaseURI = UriUtils.getBaseUri(rootDocURI);
        rootDocFileName = UriUtils.getResourceName(rootDocURI);
    }

    @Override
    public String fetchRootDoc() throws FetchException {
        if (rootDocContent == null) {
            UriFetcher fetcher = new UriFetcher(rootDocBaseURI);
            rootDocContent = fetcher.fetch(rootDocURI);
        }
        return rootDocContent;
    }

    @Override
    public String fetchResource(URI baseURI, URI resourceUri) throws FetchException {
        UriFetcher fetcher = new UriFetcher(baseURI);
        return fetcher.fetch(resourceUri);
    }

    public static interface Fetcher {
        public String fetch(URI resourceUri) throws FetchException;
    }
}