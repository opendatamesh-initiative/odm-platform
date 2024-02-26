package org.opendatamesh.platform.pp.devops.server.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "odm")
@Data
public class DevOpsConfigurations {
    
    ProductPlaneConfigs productPlane;
    UtiltyPlaneConfigs utilityPlane;

    @Data
    public static class ProductPlaneConfigs {
        ServiceConfigs registryService;
        ServiceConfigs devopsService;
        ServiceConfigs blueprintService;
        ServiceConfigs policyService;
    }

    @Data
    public static class UtiltyPlaneConfigs {
        Map<String, ExecutorServicesConfigs> executorServices;
        Map<String, ServiceConfigs> notificationServices;
    }
   
    @Data
    public static class ServiceConfigs {
        Boolean active;
        String address;
    }

    @Data
    public static class ExecutorServicesConfigs {
        Boolean active;
        String address;
        Boolean checkAfterCallback;
    }

}
