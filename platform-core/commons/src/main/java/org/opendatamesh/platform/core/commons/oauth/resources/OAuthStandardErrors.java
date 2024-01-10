package org.opendatamesh.platform.core.commons.oauth.resources;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum OAuthStandardErrors implements ODMApiStandardErrors {


    SC500_01_OAUTH_ERROR("50001", "Error creating OAuth client");

    private final String code;
    private final String description;

    OAuthStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }

    public String description() { return description; }

}
