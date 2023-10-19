package org.opendatamesh.platform.up.notification.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;

public class ErrorResource {

    @Schema(description = "Error type")
    private String errorType;

    @Schema(description = "Error message")
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