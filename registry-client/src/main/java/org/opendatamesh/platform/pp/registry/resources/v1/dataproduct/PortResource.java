package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PortResource extends ComponentResource implements Cloneable {

    @JsonProperty("promises")
    protected PromisesResource promises;

    @JsonProperty("expectations")
    protected ExpectationsResource expectations;

    @JsonProperty("contracts")
    protected ContractsResource contracts;

    @JsonProperty("tags")
    protected List<String> tags = new ArrayList<>();

    @JsonProperty("externalDocs")
    protected ExternalResourceResource externalDocs;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
