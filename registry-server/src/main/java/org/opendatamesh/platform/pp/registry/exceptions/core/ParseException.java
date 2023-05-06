package org.opendatamesh.platform.pp.registry.exceptions.core;

public class ParseException extends Exception{
    
    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable t) {
        super(message, t);
    }
}
