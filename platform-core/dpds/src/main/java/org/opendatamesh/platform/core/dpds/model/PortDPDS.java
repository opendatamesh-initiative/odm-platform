package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PortDPDS extends ComponentDPDS implements Cloneable {

    @JsonProperty("promises")
    protected PromisesDPDS promises;

    @JsonProperty("expectations")
    protected ExpectationsDPDS expectations;

    @JsonProperty("contracts")
    protected ContractsDPDS contracts;

    @JsonProperty("tags")
    protected List<String> tags = new ArrayList<>();

    @JsonProperty("externalDocs")
    protected ExternalResourceDPDS externalDocs;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
