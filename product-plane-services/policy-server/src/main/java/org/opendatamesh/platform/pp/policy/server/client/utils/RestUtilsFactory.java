package org.opendatamesh.platform.pp.policy.server.client.utils;

import org.springframework.web.client.RestTemplate;

public abstract class RestUtilsFactory {

    private RestUtilsFactory() {
        // Prevent instantiation
    }

    public static RestUtils getRestUtils(RestTemplate restTemplate) {
        return RestTemplateWrapper.wrap(restTemplate).build();
    }
}
