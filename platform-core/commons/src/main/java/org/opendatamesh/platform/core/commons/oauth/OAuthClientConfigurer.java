package org.opendatamesh.platform.core.commons.oauth;

import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

public final class OAuthClientConfigurer {

    public static AuthorizedClientServiceOAuth2AuthorizedClientManager getAuthorizedClientServiceAndManager(
            String registrationId,
            String tokenUri,
            String clientId,
            String clientSecret,
            String scope,
            String authorizationGrantType
    ) {

        ClientRegistration clientRegistration = createClientRegistration(
                registrationId,
                tokenUri,
                clientId,
                clientSecret,
                scope,
                authorizationGrantType
        );

        ClientRegistrationRepository clientRegistrationRepository = createClientRegistrationRepository(
                clientRegistration
        );

        OAuth2AuthorizedClientService oAuth2AuthorizedClientService = createOAuth2AuthorizedClientService(
                clientRegistrationRepository
        );

        return createAuthorizedClientServiceAndManager(
                clientRegistrationRepository,
                oAuth2AuthorizedClientService
        );

    }

    private static ClientRegistration createClientRegistration(
            String registrationId,
            String tokenUri,
            String clientId,
            String clientSecret,
            String scope,
            String authorizationGrantType
    ) {
        return ClientRegistration
                .withRegistrationId(registrationId)
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope(scope)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .build();
    }

    private static ClientRegistrationRepository createClientRegistrationRepository(
            ClientRegistration clientRegistration
    ) {
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }

    private static OAuth2AuthorizedClientService createOAuth2AuthorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    private static AuthorizedClientServiceOAuth2AuthorizedClientManager createAuthorizedClientServiceAndManager (
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService
                );

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;

    }

}
