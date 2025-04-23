package org.opendatamesh.platform.pp.notification.server.services.utils.rest;

import org.springframework.web.client.RestTemplate;

public abstract class RestUtilsFactory {

    private RestUtilsFactory() {
        // Prevent instantiation
    }

    public static RestUtils getRestUtils(RestTemplate restTemplate) {
        return RestTemplateWrapper.wrap(restTemplate).build();
    }
}
