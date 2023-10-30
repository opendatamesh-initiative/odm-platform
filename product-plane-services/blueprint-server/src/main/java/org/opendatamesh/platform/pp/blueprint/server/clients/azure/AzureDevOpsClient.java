package org.opendatamesh.platform.pp.blueprint.server.clients.azure;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.blueprint.server.components.OAuthTokenManager;
import org.opendatamesh.platform.pp.blueprint.server.resources.azure.AzureDevOpsRepoResource;
import org.opendatamesh.platform.pp.blueprint.server.resources.azure.TeamProjectReferenceResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class AzureDevOpsClient extends ODMClient {

    private OAuthTokenManager oAuthTokenManager;

    public AzureDevOpsClient(OAuthTokenManager oAuthTokenManager) {
        super(
                "https://dev.azure.com",
                ObjectMapperFactory.JSON_MAPPER
        );
        this.oAuthTokenManager = oAuthTokenManager;
    }

    public void createRemoteRepository(String organization, String projectName, String repositoryName) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setBearerAuth(oAuthTokenManager.getToken());

        TeamProjectReferenceResource teamProjectReferenceResource = new TeamProjectReferenceResource();
        teamProjectReferenceResource.setProjectName(projectName);
        AzureDevOpsRepoResource requestBody = new AzureDevOpsRepoResource();
        requestBody.setName(repositoryName);
        requestBody.setTeamProjectReferenceResource(teamProjectReferenceResource);

        HttpEntity requestEntity = new HttpEntity<>(requestBody, requestHeaders);

        rest.postForEntity(
                apiUrl(AzureDevOpsAPIRoutes.AZURE_DEVOPS_API_REPOS),
                requestEntity,
                String.class,
                organization,
                repositoryName
        );

    }
}
