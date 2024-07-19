package org.opendatamesh.platform.pp.params.api.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.pp.params.api.clients.ParamsClient;
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class ParamsUpdater {

    protected final ParamsClient paramsServiceClient;
    protected final ConfigurableEnvironment environment;

    protected final ResourceLoader resourceLoader;

    public ParamsUpdater(
            String paramsServiceClientAddress,
            String paramsServiceClientUUID,
            ConfigurableEnvironment environment,
            ResourceLoader resourceLoader
    ) {
        this.paramsServiceClient = new ParamsClient(
                paramsServiceClientAddress,
                paramsServiceClientUUID
        );
        this.environment = environment;
        this.resourceLoader = resourceLoader;

    }

    public void updateConfigurations() throws IOException {
        List<ParamResource> params = getParamsFromService();
        if(params != null)
            updateProperties(params);
    }

    private List<ParamResource> getParamsFromService() throws JsonProcessingException {
        ResponseEntity getParamsResponse =  paramsServiceClient.getParams();
        List<ParamResource> params = null;
        if(getParamsResponse.getStatusCode().is2xxSuccessful()) {
            ParamResource[] paramsArray = (ParamResource[]) getParamsResponse.getBody();
            params = List.of(paramsArray);
        }
        return params;
    }

    private void updateProperties(List<ParamResource> params) throws IOException {
        MapPropertySource propertySourceToUpdate = extractPropertiesFromEnvironment();
        Map<String, Object> updatedProperties = initializeUpdatedProperties(propertySourceToUpdate);
        for (ParamResource paramResource : params) {
            String propertyName = paramResource.getParamName();
            String propertyValue = paramResource.getParamValue();
            if(updatedProperties.keySet().contains(propertyName)) {
                updatedProperties.put(propertyName, propertyValue);
            }
        }
        updatePropertiesFromEnvironment(updatedProperties);
    }

    private MapPropertySource extractPropertiesFromEnvironment() throws IOException {
        Resource resource = resourceLoader.getResource(
                "classpath:application-"+environment.getActiveProfiles()[0]+".yml"
        );
        return (MapPropertySource) new YamlPropertySourceLoader().load("propertySourceToUpdate", resource).get(0);
    }

    private HashMap initializeUpdatedProperties(MapPropertySource propertySource) {
        HashMap<String, Object> props = new HashMap<>();
        for(String propertyName : propertySource.getPropertyNames()) {
            props.put(propertyName, propertySource.getProperty(propertyName));
        }
        return props;
    }

    private void updatePropertiesFromEnvironment(Map props) {
        MapPropertySource updatedPropertySource = new MapPropertySource("updatedPropertySource", props);
        environment.getPropertySources().replace(
                "application-" + environment.getActiveProfiles()[0], updatedPropertySource
        );
    }

}