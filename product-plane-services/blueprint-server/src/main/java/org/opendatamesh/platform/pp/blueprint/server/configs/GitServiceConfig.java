package org.opendatamesh.platform.pp.blueprint.server.configs;

import org.opendatamesh.platform.core.commons.git.GitConfigurer;
import org.opendatamesh.platform.core.commons.git.GitService;
import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitServiceConfig {

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
        OAuthTokenManager oAuthTokenManager = null;
        if(serviceType.equals("AZURE_DEVOPS")) {
            oAuthTokenManager = new OAuthTokenManager(
                    "azure-devops",
                    "odm-azure-devops-blueprint-principal",
                    tokenUri,
                    clientId,
                    clientSecret,
                    scope,
                    authorizationGrantType
            );
        }
        return GitConfigurer.configureGitClient(
                serviceType,
                oAuthTokenManager,
                personalAccessToken
        );
    }

}
