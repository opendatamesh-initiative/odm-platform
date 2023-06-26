package org.opendatamesh.platform.pp.registry.services;

import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductVersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProvisionServiceProxy {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProvisionServiceProxy.class);

    public ProvisionServiceProxy() {
    }

    /* 
    public InfraProvisioningTask provision(ProvisionInfo provisionInfo, InfraProvisioningTask infraProvisioningTask) {

        InfraProvisioningTaskRequestResource request = new InfraProvisioningTaskRequestResource(externalResourceMapper.toResource(provisionInfo.getTemplate()), provisionInfo.getConfigurations(), infraProvisioningTask.getOperationType());

        InfraProvisioningTaskResponseResource response = null;
        try {
            response = restTemplate.postForObject(provisionInfo.getService().getHref(), request,
                    InfraProvisioningTaskResponseResource.class);
        } catch (RestClientException e) {
            throw new BadGatewayException(
                OpenDataMeshAPIStandardError.SC502_PROVISION_SERVICE_ERROR,
                "An error occurred while comunicating with the provisionService", e);
        }

        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            response.getErrors()
                    .forEach((s, o) -> infraProvisioningTask.getDeployment().addError(s + o.toString()));
        }
        return infraProvisioningTask;
    }
    */
}
