package org.opendatamesh.platform.pp.registry.exceptions.core;

public class UnresolvableReferenceException extends Exception{
    
    public UnresolvableReferenceException(String message) {
        super(message);
    }

    public UnresolvableReferenceException(String message, Throwable t) {
        super(message, t);
    }
}
