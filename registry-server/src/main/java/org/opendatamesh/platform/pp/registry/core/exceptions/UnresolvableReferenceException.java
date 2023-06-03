package org.opendatamesh.platform.pp.registry.core.exceptions;

public class UnresolvableReferenceException extends Exception{
    
    public UnresolvableReferenceException(String message) {
        super(message);
    }

    public UnresolvableReferenceException(String message, Throwable t) {
        super(message, t);
    }
}
