package org.opendatamesh.platform.pp.blueprint.server.clients.azure;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.blueprint.server.components.OAuthTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class AzureDevOpsClient extends ODMClient {

    @Autowired
    private OAuthTokenManager oAuthTokenManager;

    public AzureDevOpsClient() {
        super(
                "https://dev.azure.com",
                ObjectMapperFactory.JSON_MAPPER
        );
    }

    public void createRemoteRepository(String organization, String repositoryName) {
        // TO DO
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        HttpEntity requestEntity = new HttpEntity<>(null, headers);

        rest.postForEntity(
                apiUrl(AzureDevOpsAPIRoutes.AZURE_DEVOPS_API_REPOS),
                requestEntity,
                String.class,
                organization,
                repositoryName
        );

    }
}
