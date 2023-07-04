package org.opendatamesh.platform.up.policy.api.v1.resources;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ErrorResource {

    // HTTP Status code
    int status;

    // Standard error code
    String code;

    // Standard error description
    String description;

    // Exception message.
    // Do not include exception cause's message.
    // It is appended only to the log error message.
    String message;

    // Service endpoint
    String path;

    // Error timestamp
    long timestamp = new Date().getTime();

}