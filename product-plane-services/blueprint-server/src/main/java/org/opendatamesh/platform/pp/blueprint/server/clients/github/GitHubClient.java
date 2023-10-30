package org.opendatamesh.platform.pp.blueprint.server.clients.github;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.server.components.OAuthTokenManager;
import org.opendatamesh.platform.pp.blueprint.server.resources.github.GitRepoResource;
import org.springframework.http.*;

public class GitHubClient extends ODMClient {

    private OAuthTokenManager oAuthTokenManager;

    public GitHubClient(OAuthTokenManager oAuthTokenManager) {
        super(
                "https://api.github.com",
                ObjectMapperFactory.JSON_MAPPER
        );
        this.oAuthTokenManager = oAuthTokenManager;
    }

    public void createRemoteRepository(String organization, String repositoryName) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setBearerAuth(oAuthTokenManager.getToken());
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        GitRepoResource requestBody = new GitRepoResource();
        requestBody.setName(repositoryName.replace("/", "")); // REMOVE REPLACE

        HttpEntity<GitRepoResource> requestEntity = new HttpEntity<>(requestBody, requestHeaders);

        ResponseEntity<String> response = rest.exchange(
                apiUrl(GitHubAPIRoutes.GITHUB_API_REPOS),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error creating remote repository: " + response.getBody()
            );
        }

    }

}
