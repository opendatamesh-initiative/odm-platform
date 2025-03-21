package org.opendatamesh.platform.pp.registry.api.resources;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class DataProductValidationRequestResource {
    @Schema(description = "If the data product descriptor syntax should be validated.", defaultValue = "true")
    private Boolean validateSyntax = true;
    @Schema(description = "If the data product descriptor should be validated using policies.", defaultValue = "true")
    private Boolean validatePolicies = true;
    @Schema(description = "If any, the data product descriptor is validated using only the policies subscribed to that events.")
    private List<String> policyEventTypes = new ArrayList<>();
    @Schema(description = "The entire data product descriptor.")
    private JsonNode dataProductVersion;

    public List<String> getPolicyEventTypes() {
        return policyEventTypes;
    }

    public void setPolicyEventTypes(List<String> policyEventTypes) {
        this.policyEventTypes = policyEventTypes;
    }

    public JsonNode getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(JsonNode dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

    public Boolean getValidateSyntax() {
        return validateSyntax;
    }

    public void setValidateSyntax(Boolean validateSyntax) {
        this.validateSyntax = validateSyntax;
    }

    public Boolean getValidatePolicies() {
        return validatePolicies;
    }

    public void setValidatePolicies(Boolean validatePolicies) {
        this.validatePolicies = validatePolicies;
    }
}
