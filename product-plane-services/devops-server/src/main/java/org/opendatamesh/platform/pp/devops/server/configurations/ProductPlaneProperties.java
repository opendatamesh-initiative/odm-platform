package org.opendatamesh.platform.pp.devops.server.configurations;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "odm.product-plane")
public class ProductPlaneProperties {
    private Map<String, Service> services;

    public Service getService(String name) {
        return services.get(name);
    }
}

@Data
class Service {
    private boolean active;
    private String address;

    // Add getters and setters for the properties

    // Rest of the class
}
