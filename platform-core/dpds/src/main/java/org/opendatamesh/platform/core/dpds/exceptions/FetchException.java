package org.opendatamesh.platform.core.dpds.exceptions;

import lombok.Data;

import java.net.URI;

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
