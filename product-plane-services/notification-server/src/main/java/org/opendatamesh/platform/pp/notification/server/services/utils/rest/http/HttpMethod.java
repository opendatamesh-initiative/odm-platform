package org.opendatamesh.platform.pp.notification.server.services.utils.rest.http;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;


    private static final Map<String, HttpMethod> mappings = new HashMap<>(16);

    static {
        for (HttpMethod httpMethod : values()) {
            mappings.put(httpMethod.name(), httpMethod);
        }
    }


    public static HttpMethod resolve(@Nullable String method) {
        return (method != null ? mappings.get(method) : null);
    }


    public boolean matches(String method) {
        return name().equals(method);
    }
}
