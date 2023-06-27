package org.opendatamesh.platform.core.dpds.parser;

import java.net.URI;
import java.net.URISyntaxException;

import org.opendatamesh.platform.core.dpds.UriFetcher;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;

import lombok.Data;

@Data
public class ParseLocation {

    private URI rootDocURI;
    private URI rootDocBaseURI;
    private String rootDocFileName;
    private String rootDocContent;

    public ParseLocation(String descriptorContent) {
        rootDocContent = descriptorContent;
    }

    public ParseLocation(URI descriptorUri) {
        rootDocURI = descriptorUri.normalize();
        String path = rootDocURI.getPath();
        String basePath = path.substring(0, path.lastIndexOf('/') + 1);
        try {
            rootDocBaseURI = new URI(
                    rootDocURI.getScheme(),
                    rootDocURI.getUserInfo(),
                    rootDocURI.getHost(),
                    rootDocURI.getPort(),
                    basePath,
                    rootDocURI.getQuery(),
                    rootDocURI.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException("An unexpected exception occured while creating base uri from uri [" + descriptorUri.toString() + "]", e);
        }
        rootDocFileName = path.substring(path.lastIndexOf('/') + 1);
    }

    public URI getBaseUri(URI resourceUri) {
        URI baseUri = null;
        URI normalizedUri = resourceUri.normalize();
        String path = normalizedUri.getPath();
        String basePath = path.substring(0, path.lastIndexOf('/') + 1);
        try {
            baseUri = new URI(
                    rootDocURI.getScheme(),
                    rootDocURI.getUserInfo(),
                    rootDocURI.getHost(),
                    rootDocURI.getPort(),
                    basePath,
                    rootDocURI.getQuery(),
                    rootDocURI.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(
                    "An unexpected exception occured while creating base uri from uri [" + resourceUri.toString() + "]",
                    e);
        }
        return baseUri;
    }

    public String fetchRootDoc() throws FetchException {
        if (rootDocContent == null) {
            UriFetcher fetcher = new UriFetcher(rootDocBaseURI);
            rootDocContent = fetcher.fetch(rootDocURI);
        }
        return rootDocContent;
    }

    public String fetchResource(URI baseURI, URI resourceUri) throws FetchException {
        UriFetcher fetcher = new UriFetcher(baseURI);
        return fetcher.fetch(resourceUri);
    }

    public static interface Fetcher {
        public String fetch(URI resourceUri) throws FetchException;
    }
}