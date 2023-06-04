package org.opendatamesh.platform.pp.registry.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.opendatamesh.platform.pp.registry.core.exceptions.FetchException;

import lombok.Data;

@Data
public class DataProductDescriptorSource {
    
    private URI rootDocURI;
    private URI rootDocBaseURI;
    private String rootDocFileName;
    private String rootDocContent;
    private Fetcher fetcher;
    
    public DataProductDescriptorSource(String descriptorContent) {
        rootDocContent = descriptorContent;
    }

    public DataProductDescriptorSource(URI descriptorUri) {
        rootDocURI = descriptorUri.normalize();
        String path = rootDocURI.getPath();
        String scheme = rootDocURI.getScheme();
        String basePath = path.substring(0, path.lastIndexOf('/') + 1);
        try {
            rootDocBaseURI = new URI(scheme + ":" + basePath);
        } catch (URISyntaxException e) {
            throw new RuntimeException("An unexpected exception occured while creating base uri [" + scheme + ":"
                    + basePath + "] of uri [" + descriptorUri.toString() + "]", e);
        }
        rootDocFileName = path.substring(path.lastIndexOf('/') + 1);

        fetcher = new UriFetcher(rootDocBaseURI);
    }

    public String fetchRootDoc() throws FetchException {
        if(rootDocContent == null) {
            rootDocContent = fetcher.fetch(rootDocURI);
        }
        return rootDocContent; 
    }

    public String fetchResource(URI resourceUri) throws FetchException {
        return fetcher.fetch(resourceUri); 
    }
    
    public Fetcher getResourceFetcher() {
        return this.fetcher;
    }    

    static public interface Fetcher {
        String fetch(URI resourceUri) throws FetchException;
    }
}
