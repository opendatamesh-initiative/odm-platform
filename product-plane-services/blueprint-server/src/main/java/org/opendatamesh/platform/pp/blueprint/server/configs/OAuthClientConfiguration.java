package org.opendatamesh.platform.pp.blueprint.server.configs;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
public class OAuthClientConfiguration {

    @Value("${git.provider}")
    private String serviceType;

    @Value("${git.oauth2.client.provider.token-uri}")
    private String tokenUri;

    @Value("${git.oauth2.client.provider.authorization-uri}")
    private String authorizationUri;

    @Value("${git.oauth2.client.provider.user-info-uri}")
    private String userInfoUri;
    @Value("${git.oauth2.client.registration.client-id}")
    private String clientId;
    @Value("${git.oauth2.client.registration.client-secret}")
    private String clientSecret;
    @Value("${git.oauth2.client.registration.scope}")
    private String scope;
    @Value("${git.oauth2.client.registration.authorization-grant-type}")
    private String authorizationGrantType;

    @Bean
    ClientRegistration oauthClientRegistration() {
        if (serviceType.equals("AZURE_DEVOPS")) {
            return ClientRegistration
                    .withRegistrationId(serviceType.toLowerCase())
                    .tokenUri(tokenUri)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .scope(scope)
                    .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                    .build();
        } else if (serviceType.equals("GITHUB")) {
            return ClientRegistration
                    .withRegistrationId(serviceType.toLowerCase())
                    .tokenUri(tokenUri)
                    .authorizationUri(authorizationUri)
                    .userInfoUri(userInfoUri)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .scope(scope)
                    .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                    .redirectUri("/oauth2/github/callback")  // Placeholder URL
                    .build();
        } else {
            throw new InternalServerException(
                    BlueprintApiStandardErrors.SC500_02_OAUTH_ERROR,
                    "Git Provider missing or unsupported, can't configure OAuth for provider [ " + serviceType + " ]"
            );
        }

    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration clientRegistration) {
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager (
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

}
