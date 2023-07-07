package org.opendatamesh.platform.core.dpds.parser.location;

import org.opendatamesh.platform.core.dpds.exceptions.FetchException;

import java.net.URI;


public interface DescriptorLocation {

    public URI getRootDocumentUri();
    public URI getRootDocumentBaseUri();
    public String getRootDocumentFileName();

    public void open() throws FetchException;
    public void close() throws FetchException;
    public String fetchRootDoc() throws FetchException;
    public String fetchResource(URI baseURI, URI resourceUri) throws FetchException;

    public static interface Fetcher {
        /**
         * 
         * @param baseUri the base uri used to resolve the resource uri. Must be absolute
         * @param resourceUri the of the resource to fetch. It should be relative. 
         * If it is absolute the base uri is ignored. No exception is thrown. 
         * Anyway in this case is better to just call the fetch method below.
         * @return the content fatched from uri
         * 
         * @throws FetchException
         */
        public String fetch(URI baseUri, URI resourceUri) throws FetchException;
        public String fetch(URI resourceUri) throws FetchException;
    }
}