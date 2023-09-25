package org.opendatamesh.platform.core.dpds.parser.location;

import lombok.Data;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;

import java.net.URI;
import java.util.List;

@Data
public class UriLocation implements DescriptorLocation {

    protected boolean opened;

    private URI rootDocumentUri;
    private URI rootDocumentBaseUri;
    private String rootDocumentFileName;
    private String rootDocContent;

    private UriFetcher fetcher;

    public UriLocation(String descriptorContent) {
        rootDocContent = descriptorContent;
        fetcher = new UriFetcher();
        opened = true; 
    }

    public UriLocation(URI descriptorUri) {
        setDescriptorUri(descriptorUri);
        fetcher = new UriFetcher();
        opened = true; 
    }

    UriLocation() {
        fetcher = new UriFetcher();
        opened = true; 
    }

    void setDescriptorUri(URI descriptorUri) {
        rootDocumentUri = descriptorUri.normalize();
        rootDocumentBaseUri = UriUtils.getResourcePathUri(rootDocumentUri);
        rootDocumentFileName = UriUtils.getResourceName(rootDocumentUri);
    }

    @Override
    public String fetchRootDoc() throws FetchException {
        if(opened == false) {
            throw new FetchException("Impossible to fecth a closed location", rootDocumentUri);
        }

        if (rootDocContent == null) {
            rootDocContent = fetcher.fetch(rootDocumentBaseUri, rootDocumentUri);
        }
        return rootDocContent;
    }

    @Override
    public String fetchResource(URI baseURI, URI resourceUri) throws FetchException {
        if(opened == false) {
            throw new FetchException("Impossible to fecth a closed location", resourceUri);
        }
        return fetcher.fetch(baseURI, resourceUri);
    }

    @Override
    public String fetchResource(URI resourceUri) throws FetchException {
        if(opened == false) {
            throw new FetchException("Impossible to fecth a closed location", resourceUri);
        }
        return fetcher.fetch(resourceUri);
    }

    public void setEncoding(String encoding) {
        fetcher.setEncoding(encoding);
    }

     public String getEncoding() {
        return fetcher.getEncoding();
    }

    public void setAuthorizationsValues(List<AuthorizationValue> authorizationValues) {
       fetcher.setAuthorizationValues(authorizationValues);
    }

    public static interface Fetcher {
        public String fetch(URI resourceUri) throws FetchException;
    }

    @Override
    public void open() throws FetchException {
        opened = true; 
    }

    @Override
    public void close() throws FetchException {
         opened = false; 
    }
}