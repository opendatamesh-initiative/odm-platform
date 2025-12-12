package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.server.ODMPolicyApp;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.annotation.PostConstruct;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ODMPolicyApp.class, TestContainerConfig.class, TestConfig.class})
@ActiveProfiles("test")
public abstract class PolicyApplicationIT {

    @LocalServerPort
    protected String port;

    protected TestRestTemplate rest;

    @PostConstruct
    public final void init() {
        rest = new TestRestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(30000);
        requestFactory.setConnectTimeout(30000);
        rest.getRestTemplate().setRequestFactory(requestFactory);
        // add uri template handler because '+' of iso date would not be encoded
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        rest.setUriTemplateHandler(defaultUriBuilderFactory);
    }

    protected String apiUrl(Routes route) {
        return apiUrl(route, "");
    }

    protected String apiUrl(Routes route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    protected String apiUrlOfItem(Routes route) {
        return apiUrl(route, "/{id}");
    }

    protected String apiUrlFromString(String routeUrlString) {
        return "http://localhost:" + port + routeUrlString;
    }
}

