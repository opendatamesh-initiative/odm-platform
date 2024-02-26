package org.opendatamesh.platform.pp.blueprint.server.configs;

import org.opendatamesh.platform.pp.params.api.components.ParamsUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "odm.productPlane.paramsService.active", havingValue = "true")
public class PropertyUpdaterConfig {

    @Value("${odm.productPlane.paramsService.address}")
    private String odmParamsServerAddress;
    @Value("${odm.productPlane.paramsService.clientUUID}")
    private String odmParamsClientUUID;

    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void updateProperties() throws IOException {
        ParamsUpdater paramsUpdater = new ParamsUpdater(
                odmParamsServerAddress,
                odmParamsClientUUID,
                environment,
                resourceLoader
        );
        paramsUpdater.updateConfigurations();
        System.out.println(environment);
    }

}
