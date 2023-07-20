package org.opendatamesh.platform.up.notification.api.resources;

public class ErrorResource {

    private String errorType;
    private String message;

    public ErrorResource() {
    }

    public ErrorResource(String errorType, String message) {
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