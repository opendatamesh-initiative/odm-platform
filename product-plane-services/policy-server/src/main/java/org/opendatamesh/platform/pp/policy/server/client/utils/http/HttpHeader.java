package org.opendatamesh.platform.pp.policy.server.client.utils.http;


public class HttpHeader {
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";

    private String name;
    private String value;

    public HttpHeader() {
    }

    public HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpHeader bearerAuth(String token) {
        return new HttpHeader(AUTHORIZATION, "Bearer " + token);
    }

    public static HttpHeader contentType(MediaType mediaType) {
        return new HttpHeader(CONTENT_TYPE, mediaType.getFullType());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

