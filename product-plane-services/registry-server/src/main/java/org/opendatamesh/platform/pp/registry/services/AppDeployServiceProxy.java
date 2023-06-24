package org.opendatamesh.platform.pp.registry.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AppDeployServiceProxy {
    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ProvisionServiceProxy.class);

    public AppDeployServiceProxy() {
    }

    /* 
    public AppDeployingTaskResponse GET(AppDeployingTask appDeployingTask) {

        AppDeployingTaskResponse response = appDeployingTask.getAppDeployingTaskResponse();

        AppDeployingTaskRequest request = appDeployingTask.getAppDeployingTaskRequest();
        String deploymentServiceUrl = request.getDeploymentServiceUrl();
        if (deploymentServiceUrl == null) {
            throw new RuntimeException(
                    "Invalid reference to deployment service for application integration with id: '" +
                            appDeployingTask.getId() + "' for deployment with id: '"
                            + appDeployingTask.getDeployment().getId() + "'");
        }
        try {
            // TODO perchè non basta deploymentServiceUrl? Non è uniforme con il
            // ProvisionService
            response = restTemplate.getForObject(
                    deploymentServiceUrl + "/" + response.getIntegrationServiceId(),
                    AppDeployingTaskResponse.class);
        } catch (RestClientException e) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_DEPLOY_SERVICE_ERROR,
                    "An error occurred while comunicating with the deployService", e);
        }

        return response;
    }
    */
}
