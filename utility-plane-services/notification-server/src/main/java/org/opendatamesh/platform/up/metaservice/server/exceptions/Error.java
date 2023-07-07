package org.opendatamesh.platform.up.metaservice.server.exceptions;

public class Error {

    private String errorType;
    private String message;

    public Error() {
    }

    public Error(String errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "errorType='" + errorType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}