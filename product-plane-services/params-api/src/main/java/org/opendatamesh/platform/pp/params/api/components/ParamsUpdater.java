package org.opendatamesh.platform.pp.params.api.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.platform.pp.params.api.clients.ParamsClient;
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public final class ParamsUpdater {

    protected final ParamsClient paramServiceClient;
    protected final ConfigurableEnvironment configurableEnvironment;

    public ParamsUpdater(ParamsClient paramServiceClient, ConfigurableEnvironment configurableEnvironment) {
        this.paramServiceClient = paramServiceClient;
        this.configurableEnvironment = configurableEnvironment;
    }

    public void updateConfigurations() throws JsonProcessingException {
        String activeProfile = configurableEnvironment.getActiveProfiles()[0];
        for (PropertySource<?> propertySource : configurableEnvironment.getPropertySources()) {
            if (propertySource.getName().startsWith("applicationConfig:") &&
                    propertySource.getName().contains(activeProfile)) {
                Map<String, Object> properties = extractProperties((EnumerablePropertySource<?>) propertySource);
                updateProperties(properties);
            }
        }
    }

    private Map<String, Object> extractProperties(EnumerablePropertySource<?> propertySource) {
        Map<String, Object> properties = new HashMap<>();
        for (String propertyName : propertySource.getPropertyNames()) {
            Object propertyValue = propertySource.getProperty(propertyName);
            properties.put(propertyName, propertyValue);
        }
        return properties;
    }

    protected void updateProperties(Map<String, Object> properties) throws JsonProcessingException {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            ResponseEntity paramServiceResponse = paramServiceClient.getOneParamByName(propertyName);
            if (paramServiceResponse.getStatusCode().is2xxSuccessful()) {
                ParamResource paramResource = (ParamResource) paramServiceResponse.getBody();
                String updatedValue = paramResource.getParamValue();
                if(updatedValue != null)
                    updateProperty(propertyName, updatedValue);
            }
        }
    }

    protected void updateProperty(String propertyName, String propertyValue) {
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        propertySources.addFirst(new CustomPropertySource(propertyName, propertyValue));
    }

    // Custom PropertySource class
    private static class CustomPropertySource extends PropertySource<String> {
        CustomPropertySource(String name, String source) {
            super(name, source);
        }

        @Override
        public Object getProperty(String name) {
            return null; // Not used
        }
    }

}
