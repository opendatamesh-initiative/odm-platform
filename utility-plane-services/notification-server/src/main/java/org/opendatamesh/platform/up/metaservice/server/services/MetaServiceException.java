package org.opendatamesh.platform.up.metaservice.server.services;

public class MetaServiceException extends Exception {
    public MetaServiceException(String message) {
        super(message);
    }

    public MetaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
