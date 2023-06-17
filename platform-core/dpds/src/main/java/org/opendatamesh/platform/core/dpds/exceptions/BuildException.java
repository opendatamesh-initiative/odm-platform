package org.opendatamesh.platform.core.dpds.exceptions;

import lombok.Data;

@Data
public class BuildException extends Exception {
    
    Stage stage;

    public BuildException(String message, Stage stage) {
        super(message);
        this.stage = stage;
    }

    public BuildException(String message, Stage stage, Throwable t) {
        super(message, t);
        this.stage = stage;
    }

    static public enum Stage {
        LOAD_ROOT_DOC, 
        RESOLVE_EXTERNAL_REFERENCES, 
        RESOLVE_INTERNAL_REFERENCES,
        RESOLVE_READ_ONLY_PROPERTIES,
        RESOLVE_STANDARD_DEFINITIONS, 
        RESOLVE_TEMPLATE_PROPERTIES
    }
}
