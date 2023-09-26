package org.opendatamesh.platform.up.policy.api.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.opendatamesh.platform.up.policy.api.v1.errors.PolicyserviceOpaAPIStandardError;

import java.util.Date;

@Data
public class ErrorResource {

    // HTTP Status code
    @Schema(description = "HTTP numeric status code")
    int status;

    // Standard error code
    @Schema(description = "HTTP textual status code")
    String code;

    // Standard error description
    @Schema(description = "Error description")
    String description;

    // Exception message.
    // Do not include exception cause's message.
    // It is appended only to the log error message.
    @Schema(description = "Error message")
    String message;

    // Service endpoint
    @Schema(description = "Endpoint which generate the Error")
    String path;

    // Error timestamp
    @Schema(description = "Error timestamp")
    Long timestamp;

    public ErrorResource() {
        this.timestamp = new Date().getTime();
    }

    public ErrorResource(int status, PolicyserviceOpaAPIStandardError error, String message, String path) {
        super();
        this.status = status;
        this.code = error.code();
        this.description = error.description();
        this.message = message;
        this.path = path;
    }

    public ErrorResource(int status, String errorCode, String errorDescription, String message, String path) {
        super();
        this.status = status;
        this.code = errorCode;
        this.description = errorDescription;
        this.message = message;
        this.path = path;
    }
}