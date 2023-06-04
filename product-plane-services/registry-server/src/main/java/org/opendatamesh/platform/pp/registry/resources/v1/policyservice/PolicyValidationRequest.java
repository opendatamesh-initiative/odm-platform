package org.opendatamesh.platform.pp.registry.resources.v1.policyservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Convert;

import org.opendatamesh.platform.pp.registry.utils.HashMapConverter;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyValidationRequest {

    @JsonProperty("input")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> input=new HashMap<>();

    public PolicyValidationRequest() {
    }

    public PolicyValidationRequest(Map<String, Object> input) {
        this.input = input;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "PolicyInput{" +
                "input=" + input +
                '}';
    }
}
