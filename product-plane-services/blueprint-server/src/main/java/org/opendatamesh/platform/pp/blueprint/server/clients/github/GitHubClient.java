package org.opendatamesh.platform.pp.blueprint.server.clients.github;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

public class GitHubClient extends ODMClient {

    public GitHubClient() {
        super(
                "https://api.github.com/",
                ObjectMapperFactory.JSON_MAPPER
        );
    }

    public void createRemoteRepository(String username, String repositoryName) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Bearer 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");
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", repositoryName.replace("/",""));

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, requestHeaders);

        ResponseEntity response = rest.exchange(
                apiUrl(GitHubAPIRoutes.GITHUB_API_REPOSITORIES),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_01_GIT_ERROR,
                    "Error creating repository: " + response.getBody()
            );
        }

    }

}
