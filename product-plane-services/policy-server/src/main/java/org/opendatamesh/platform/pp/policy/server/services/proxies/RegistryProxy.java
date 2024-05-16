package org.opendatamesh.platform.pp.policy.server.services.proxies;

import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegistryProxy {

    private RegistryClient registryClient;

    private static final Logger logger = LoggerFactory.getLogger(DevOpsProxy.class);

    public RegistryProxy(
            @Value("${odm.productPlane.registryService.active}") Boolean registryServiceActive,
            @Value("${odm.productPlane.registryService.address}") String registryServerAddress
    ) {
        if(registryServiceActive == null | registryServerAddress == null) {
            throw new NullPointerException(
                    "Missing at least one of required configurations odm.productPlane.registryService.active "
                            + "and odm.productPlane.registryService.address"
            );
        }
        if(registryServiceActive) {
            this.registryClient = new RegistryClient(registryServerAddress);
        } else {
            this.registryClient = null;
        }
    }

}
