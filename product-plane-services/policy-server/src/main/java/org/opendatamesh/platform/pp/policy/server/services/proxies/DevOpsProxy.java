package org.opendatamesh.platform.pp.policy.server.services.proxies;

import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DevOpsProxy {

    private DevOpsClient devOpsClient;

    private static final Logger logger = LoggerFactory.getLogger(DevOpsProxy.class);

    public DevOpsProxy(
            @Value("${odm.productPlane.devopsService.active}") Boolean devOpsServiceActive,
            @Value("${odm.productPlane.devopsService.address}") String devOpsServerAddress
    ) {
        if(devOpsServiceActive == null | devOpsServerAddress == null) {
            throw new NullPointerException(
                    "Missing at least one of required configurations odm.productPlane.devopsService.active "
                            + "and odm.productPlane.devopsService.address"
            );
        }
        if(devOpsServiceActive) {
            this.devOpsClient = new DevOpsClient(devOpsServerAddress);
        } else {
            this.devOpsClient = null;
        }
    }

    public ActivityResource getActivityById(String id) {
        Long longId = Long.valueOf(id);
        ActivityResource activityResource = null;
        if(devOpsClient != null) {
            try {
                activityResource = devOpsClient.readActivity(longId);
            } catch (Exception e) {
                logger.warn("Error fetching ActivityResource from DevOps Server", e);
            }
        } else {
            logger.info("DevOps Service not active, skipping interaction with it.");
        }
        return activityResource;
    }

}
