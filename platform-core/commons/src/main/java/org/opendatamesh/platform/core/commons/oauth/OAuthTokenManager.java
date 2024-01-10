package org.opendatamesh.platform.core.commons.oauth;

import org.opendatamesh.platform.core.commons.oauth.resources.OAuthStandardErrors;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Objects;

public class OAuthTokenManager {

    private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager;

    private String clientRegistrationId;

    private String principal;

    public OAuthTokenManager(
            String clientRegistrationId,
            String principal,
            String tokenUri,
            String clientId,
            String clientSecret,
            String scope,
            String authorizationGrantType
    ) {
        this.clientRegistrationId = clientRegistrationId;
        this.principal = principal;
        try {
            this.authorizedClientServiceAndManager = OAuthClientConfigurer.getAuthorizedClientServiceAndManager(
                    clientRegistrationId,
                    tokenUri,
                    clientId,
                    clientSecret,
                    scope,
                    authorizationGrantType
            );
        } catch (Throwable t) {
            throw new InternalServerException(
                    OAuthStandardErrors.SC500_01_OAUTH_ERROR,
                    "Error configuring OAuth",
                    t
            );
        }
    }

    public String getToken() {

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistrationId)
                .principal(principal)
                .build();

        OAuth2AuthorizedClient authorizedClient = this.authorizedClientServiceAndManager.authorize(authorizeRequest);
        OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();

        return accessToken.getTokenValue();

    }

}