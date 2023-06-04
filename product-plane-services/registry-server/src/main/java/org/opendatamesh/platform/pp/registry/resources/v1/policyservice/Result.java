package org.opendatamesh.platform.pp.registry.resources.v1.policyservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    @JsonProperty("allow")
    private boolean allow;

    public Result() {
    }

    public Result(boolean allow) {
        this.allow = allow;
    }

    @Override
    public String toString() {
        return "Result{" +
                "allow=" + allow +
                '}';
    }

    public boolean getAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }
}
