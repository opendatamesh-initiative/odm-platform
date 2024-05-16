package org.opendatamesh.platform.core.commons.servers.exceptions;

public interface ODMApiStandardErrors {
    String code();
    String description();

    static ODMApiStandardErrors getNotFoundError(String className) {
        return null;
    };
}
