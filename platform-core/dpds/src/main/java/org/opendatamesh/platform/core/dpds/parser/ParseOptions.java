package org.opendatamesh.platform.core.dpds.parser;

import lombok.Data;

@Data
public class ParseOptions {
    // the base url of ODM Registry API. 
    // Used to rewrite reference to API, Schemas and templates
    private  String serverUrl;

    private boolean validateRootDocumet = true;
    private boolean validateExternalRefs = true;
    private boolean validateInternalRefs = true;
}
