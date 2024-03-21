package org.opendatamesh.platform.pp.policy.server.config;

import lombok.Data;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class OdmClients {

    @Value("${odm.productPlane.devopsService.active}")
    private Boolean devOpsServiceActive;

    @Value("${odm.productPlane.devopsService.address}")
    private String devOpsServerAddress;

    private DevOpsClient devOpsClient;

    public OdmClients(){
        if(devOpsServiceActive)
            this.devOpsClient = new DevOpsClient(devOpsServerAddress);
        else
            this.devOpsClient = null;
    }

}
