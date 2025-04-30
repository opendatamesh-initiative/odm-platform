package org.opendatamesh.platform.pp.policy.server.client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.opendatamesh.platform.pp.policy.server.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.pp.policy.server.client.utils.http.HttpEntity;
import org.opendatamesh.platform.pp.policy.server.client.utils.http.HttpHeader;
import org.opendatamesh.platform.pp.policy.server.client.utils.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

class RestTemplateWrapper implements RestUtilsTemplate {

    private final RestTemplate restTemplate;

    private RestTemplateWrapper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static RestTemplateWrapper wrap(RestTemplate restTemplate) {
        return new RestTemplateWrapper(restTemplate);
    }

    public RestUtils build() {
        RestUtilsTemplate template = this;
        return new BaseRestUtils(template);
    }

    @Override
    public <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws ClientException {
        try {
            LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            requestEntity.getRawHeaders().forEach(headers::add);
            return restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.resolve(method.name()),
                    new org.springframework.http.HttpEntity<>(requestEntity.getBody(), headers),
                    responseType,
                    uriVariables
            ).getBody();
        } catch (RestClientResponseException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new ClientException(500, e.getMessage());
        }
    }

    @Override
    public <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws ClientException {
        try {
            LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            requestEntity.getRawHeaders().forEach(headers::add);
            return restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.resolve(method.name()),
                    new org.springframework.http.HttpEntity<>(requestEntity.getBody(), headers),
                    responseType,
                    uriVariables
            ).getBody();
        } catch (RestClientResponseException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new ClientException(500, e.getMessage());
        }
    }

    @Override
    public File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation) throws ClientException {
        try {
            return restTemplate.execute(url, org.springframework.http.HttpMethod.POST, request -> {
                if (httpHeaders != null) {
                    LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                    httpHeaders.forEach(h -> headers.put(h.getName(), Lists.newArrayList(h.getValue())));
                    request.getHeaders().addAll(headers);
                }
                if (resource != null) {
                    new ObjectMapper().writeValue(request.getBody(), resource);
                }
            }, response -> {
                try (FileOutputStream fos = new FileOutputStream(storeLocation)) {
                    StreamUtils.copy(response.getBody(), fos);
                    return storeLocation;
                }
            });
        } catch (RestClientResponseException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new ClientException(500, e.getMessage());
        }
    }
}
