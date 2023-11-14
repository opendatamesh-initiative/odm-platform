package org.opendatamesh.platform.pp.blueprint.server.clients.azure;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.server.components.OAuthTokenManager;
import org.opendatamesh.platform.pp.blueprint.server.resources.azure.AzureDevOpsRepoResource;
import org.opendatamesh.platform.pp.blueprint.server.resources.azure.TeamProjectReferenceResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AzureDevOpsClient extends ODMClient {

    private OAuthTokenManager oAuthTokenManager;

    public AzureDevOpsClient(OAuthTokenManager oAuthTokenManager) {
        super(
                "https://dev.azure.com",
                ObjectMapperFactory.JSON_MAPPER
        );
        this.oAuthTokenManager = oAuthTokenManager;
    }

    public void createRemoteRepository(String organization, String projectId, String repositoryName) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setBearerAuth(oAuthTokenManager.getToken());

        TeamProjectReferenceResource teamProjectReferenceResource = new TeamProjectReferenceResource();
        teamProjectReferenceResource.setProjectId(projectId);
        AzureDevOpsRepoResource requestBody = new AzureDevOpsRepoResource();
        requestBody.setName(repositoryName);
        requestBody.setTeamProjectReferenceResource(teamProjectReferenceResource);

        HttpEntity requestEntity = new HttpEntity<>(requestBody, requestHeaders);

        ResponseEntity<String> response = rest.postForEntity(
                apiUrl(AzureDevOpsAPIRoutes.AZURE_DEVOPS_API_REPOS),
                requestEntity,
                String.class,
                organization,
                projectId
        );

        // IMPROVE IT
        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error creating remote repository: " + response.getBody()
            );
        }

    }
}
