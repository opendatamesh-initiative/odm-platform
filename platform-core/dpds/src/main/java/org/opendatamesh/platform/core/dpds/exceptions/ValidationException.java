package org.opendatamesh.platform.core.dpds.exceptions;

import com.networknt.schema.ValidationMessage;
import lombok.Data;

import java.util.Set;

@Data
public class ValidationException extends Exception {
    Set<ValidationMessage> errors;

    public ValidationException(String message, Set<ValidationMessage> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(String message, Set<ValidationMessage> errors, Throwable t) {
        super(message, t);
        this.errors = errors;
    }
}
