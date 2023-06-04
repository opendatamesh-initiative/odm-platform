package org.opendatamesh.platform.pp.registry.core.exceptions;

import java.net.URI;

import lombok.Data;

@Data
public class FetchException extends Exception{
    
    URI uri;

    public FetchException(String message, URI uri) {
        super(message);
        this.uri = uri;
    }

    public FetchException(String message, URI uri, Throwable t) {
        super(message, t);
        this.uri = uri;
    }
}
