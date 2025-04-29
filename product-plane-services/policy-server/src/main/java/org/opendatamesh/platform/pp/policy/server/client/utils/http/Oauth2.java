package org.opendatamesh.platform.pp.policy.server.client.utils.http;

public class Oauth2 {
    private String url;
    private String grantType;
    private String scope;
    private String clientId;
    private String clientSecret;
    private String clientCertificate;
    private String clientCertificatePrivateKey;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(String clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public String getClientCertificatePrivateKey() {
        return clientCertificatePrivateKey;
    }

    public void setClientCertificatePrivateKey(String clientCertificatePrivateKey) {
        this.clientCertificatePrivateKey = clientCertificatePrivateKey;
    }
}

