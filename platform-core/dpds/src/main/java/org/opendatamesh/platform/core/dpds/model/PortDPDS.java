package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class PortDPDS extends ComponentDPDS {

    @JsonProperty("promises")
    protected PromisesDPDS promises;

    @JsonProperty("expectations")
    protected ExpectationsDPDS expectations;

    @JsonProperty("contracts")
    protected ContractsDPDS contracts;

    public boolean hasPromises() {
        return promises != null;
    }

    public boolean hasApi() {
        return hasPromises() && promises.getApi() != null;
    }

    public boolean hasApiDefinition() {
        return hasApi() && promises.getApi().getDefinition() != null;
    }

    public PortDPDS cloneWithoutrawContent() {  
        PortDPDS clonedPort = clone();
        clonedPort.setRawContent(null);
        if(clonedPort.hasApiDefinition()){
            clonedPort.getPromises().getApi().getDefinition().setRawContent(null);
        }
        return clonedPort;
    }  

    @Override
    public PortDPDS clone() { 
        PortDPDS clonedPort = null; 
        try {
            clonedPort =  (PortDPDS)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }  
        return clonedPort;
    }  

    


}

