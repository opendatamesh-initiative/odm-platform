package org.opendatamesh.platform.core.commons.git;

import org.opendatamesh.dpds.location.GitService;
import org.opendatamesh.platform.core.commons.git.resources.errors.GitStandardErrors;
import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;

public final class GitServiceFactory {

    private GitServiceFactory() {
    }

    public static GitService configureGitClient(
            String gitProvider,
            OAuthTokenManager oAuthTokenManager
    ) {
        if (gitProvider.equals("AZURE_DEVOPS")) {
            return new AzureService(oAuthTokenManager);
        }
        throw new InternalServerException(
                GitStandardErrors.SC500_02_GIT_CLIENT_ERROR,
                "Oauth not supported for this Git Provider [" + gitProvider + "]"
        );
    }

    public static GitService configureGitClient(
            String gitProvider,
            String username,
            String personalAccessToken
    ) {
        switch (gitProvider) {
            case "AZURE_DEVOPS":
                throw new InternalServerException(
                        GitStandardErrors.SC500_02_GIT_CLIENT_ERROR,
                        "Configuration not valid for AZURE_DEVOPS git client (only oauth is supported)."
                );
            case "GITHUB":
                return new GitHubService(username, personalAccessToken);
            case "GENERIC":
                return new GenericGitService(username, personalAccessToken);
            default:
                throw new InternalServerException(
                        GitStandardErrors.SC500_02_GIT_CLIENT_ERROR,
                        "Impossibile to initialize GitService - unknown Git Provider [" + gitProvider + "]"
                );
        }
    }
}
