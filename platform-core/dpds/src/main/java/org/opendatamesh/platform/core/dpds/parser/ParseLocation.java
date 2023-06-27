package org.opendatamesh.platform.core.dpds.parser;

import java.net.URI;

import org.opendatamesh.platform.core.dpds.exceptions.FetchException;


public interface ParseLocation {

    public URI getRootDocURI();
    public URI getRootDocBaseURI();
    public String getRootDocFileName();

    public String fetchRootDoc() throws FetchException;
    public String fetchResource(URI baseURI, URI resourceUri) throws FetchException;

    public static interface Fetcher {
        public String fetch(URI resourceUri) throws FetchException;
    }
}