package org.opendatamesh.platform.pp.blueprint.server.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.pp.params.api.clients.ParamsClient;
import org.opendatamesh.platform.pp.params.api.components.ParamsUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(name = "odm.productPlane.paramsService.active", havingValue = "true")
public class PropertyUpdaterConfig {

    @Value("${odm.productPlane.paramsService.address}")
    private String odmParamsServerAddress;
    @Value("${odm.productPlane.paramsService.clientUUID}")
    private String odmParamsClientUUID;

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    @PostConstruct
    public void updateProperties() throws JsonProcessingException {
        ParamsClient paramsClient = new ParamsClient(
                odmParamsServerAddress,
                odmParamsClientUUID
        );
        ParamsUpdater paramsUpdater = new ParamsUpdater(
                paramsClient,
                configurableEnvironment
        );
        paramsUpdater.updateConfigurations();
    }

}
