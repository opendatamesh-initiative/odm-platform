package org.opendatamesh.platform.core.commons.git;

import org.opendatamesh.platform.core.commons.git.providers.azuredevops.AzureService;
import org.opendatamesh.platform.core.commons.git.providers.generic.GenericGitService;
import org.opendatamesh.platform.core.commons.git.providers.github.GitHubService;
import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;

public final class GitConfigurer {

    public static GitService configureGitClient(
            String gitProvider,
            OAuthTokenManager oAuthTokenManager,
            String personalAccessToken
    ) {
        switch (gitProvider) {
            case "AZURE_DEVOPS":
                return new AzureService(oAuthTokenManager);
            case "GITHUB":
                return new GitHubService(personalAccessToken);
            case "GENERIC":
                return new GenericGitService();
            default:
                throw new InternalServerException(
                        GitStandardErrors.SC500_02_GIT_CLIENT_ERROR,
                        "Impossibile to initialize GitService - unknown Git Provider [" + gitProvider + "]"
                );
        }
    }

}
