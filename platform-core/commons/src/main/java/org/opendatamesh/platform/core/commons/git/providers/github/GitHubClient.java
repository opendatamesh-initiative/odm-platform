package org.opendatamesh.platform.core.commons.git.providers.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.git.GitStandardErrors;
import org.opendatamesh.platform.core.commons.git.resources.github.GitRepoResource;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.springframework.http.*;

public class GitHubClient extends ODMClient {

    private String personalAccessToken;

    public GitHubClient(String personalAccessToken) {
        super(
                "https://api.github.com",
                new ObjectMapper()
        );
        this.personalAccessToken = personalAccessToken;
    }

    public void createRemoteRepository(String organization, String repositoryName) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setBearerAuth(personalAccessToken);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        GitRepoResource requestBody = new GitRepoResource();
        requestBody.setName(repositoryName);

        HttpEntity<GitRepoResource> requestEntity = new HttpEntity<>(requestBody, requestHeaders);

        ResponseEntity<String> response = rest.exchange(
                apiUrl(GitHubAPIRoutes.GITHUB_API_REPOS),
                HttpMethod.POST,
                requestEntity,
                String.class
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
                            "Operation forbidden for given user - " + response.getBody()
                    );
                default:
                    throw new InternalServerException(
                            GitStandardErrors.SC500_01_GIT_ERROR,
                            "Error creating remote repository - " + response.getBody()
                    );
            }
        }

    }

}
