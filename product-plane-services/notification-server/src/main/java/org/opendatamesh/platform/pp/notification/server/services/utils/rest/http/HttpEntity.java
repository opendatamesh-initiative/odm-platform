package org.opendatamesh.platform.pp.notification.server.services.utils.rest.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpEntity<T> {

    public static final HttpEntity<?> EMPTY = new HttpEntity<>();

    private final List<HttpHeader> headers = new ArrayList<>();
    private final T body;

    protected HttpEntity() {
        this(null, null);
    }

    public HttpEntity(T body) {
        this(body, null);
    }

    public HttpEntity(T body, List<HttpHeader> headers) {

        this.body = body;
        if (headers != null) {
            this.headers.addAll(headers);
        }
    }

    public List<HttpHeader> getHeaders() {
        return headers;
    }

    public Map<String, String> getRawHeaders() {
        return headers.stream().collect(Collectors.toMap(HttpHeader::getName, HttpHeader::getValue));
    }

    public T getBody() {
        return body;
    }
}