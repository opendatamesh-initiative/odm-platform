package org.opendatamesh.platform.core.commons.git.providers.azuredevops;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.git.GitStandardErrors;
import org.opendatamesh.platform.core.commons.git.resources.azure.AzureDevOpsRepoResource;
import org.opendatamesh.platform.core.commons.git.resources.azure.TeamProjectReferenceResource;
import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AzureDevOpsClient extends ODMClient {

    private OAuthTokenManager oAuthTokenManager;

    public AzureDevOpsClient(OAuthTokenManager oAuthTokenManager) {
        super(
                "https://dev.azure.com",
                new ObjectMapper()
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

        if(!response.getStatusCode().is2xxSuccessful()) {
            switch (response.getStatusCode()) {
                case UNAUTHORIZED:
                    throw new InternalServerException(
                            GitStandardErrors.SC401_01_GIT_ERROR,
                            "User unauthorized - " + response.getBody()
                    );
                case FORBIDDEN:
                    throw new InternalServerException(
                            GitStandardErrors.SC403_01_GIT_ERROR,
                            "User authentication failed - " + response.getBody()
                    );
                case CONFLICT:
                    throw new InternalServerException(
                            GitStandardErrors.SC409_01_GIT_CONFLICT,
                            "Resource already exists - " + response.getBody()
                    );
                default:
                    throw new InternalServerException(
                            GitStandardErrors.SC500_01_GIT_ERROR,
                            "Error creating remote repository: " + response.getBody()
                    );
            }
        }

    }
}
