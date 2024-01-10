package org.opendatamesh.platform.pp.blueprint.server.configs;

import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;
import org.opendatamesh.platform.pp.blueprint.server.services.git.AzureService;
import org.opendatamesh.platform.pp.blueprint.server.services.git.GitHubService;
import org.opendatamesh.platform.pp.blueprint.server.services.git.GitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitServiceFactoryConfig {

    @Value("${git.provider}")
    private String serviceType;

    @Value("${git.auth.oauth2.client.provider.token-uri}")
    private String tokenUri;

    /*@Value("${git.auth.oauth2.client.provider.authorization-uri}")
    private String authorizationUri;

    @Value("${git.auth.oauth2.client.provider.user-info-uri}")
    private String userInfoUri;*/
    @Value("${git.auth.oauth2.client.registration.client-id}")
    private String clientId;

    @Value("${git.auth.oauth2.client.registration.client-secret}")
    private String clientSecret;

    @Value("${git.auth.oauth2.client.registration.scope}")
    private String scope;

    @Value("${git.auth.oauth2.client.registration.authorization-grant-type}")
    private String authorizationGrantType;

    @Value("${git.auth.pat}")
    private String personalAccessToken;

    @Bean
    public GitService gitService() {
        switch (serviceType) {
            case "AZURE_DEVOPS":
                OAuthTokenManager oAuthTokenManager = new OAuthTokenManager(
                        "azure-devops",
                        "odm-azure-devops-blueprint-principal",
                        tokenUri,
                        clientId,
                        clientSecret,
                        scope,
                        authorizationGrantType
                );
                return new AzureService(oAuthTokenManager);
            case "GITHUB":
                return new GitHubService(personalAccessToken);
            default:
                throw new RuntimeException(
                        "Impossibile to initialize GitService - unknown Git Provider [" + serviceType + "]"
                );
        }
    }

}
