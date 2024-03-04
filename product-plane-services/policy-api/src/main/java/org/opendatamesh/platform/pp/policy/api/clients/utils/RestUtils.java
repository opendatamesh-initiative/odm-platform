package org.opendatamesh.platform.pp.policy.api.clients.utils;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;

public class RestUtils {

    private final TestRestTemplate rest;

    public RestUtils(TestRestTemplate restTemplate) {
        this.rest = restTemplate;
    }

    public <R, F> Page<R> getPage(String url, Pageable pageable, F filters) {
        try {
            ParameterizedTypeReference<Page<R>> responseType = new ParameterizedTypeReference<>() {
            };

            if (pageable != null) {
                url = appendQueryStringFromPageable(url, pageable);
            }
            if (filters != null) {
                url = appendQueryStringFromFilters(url, filters);
            }

            ResponseEntity<Page<R>> responseEntity = rest.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    responseType
            );
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                //TODO throw exc
            }
            return responseEntity.getBody();

        } catch (Exception e) {
            //TODO
            return null;
        }
    }

    private String appendQueryStringFromPageable(String url, Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());
        StringBuilder sb = new StringBuilder();
        pageable.getSort().forEach(order -> sb.append(order.getProperty()).append(",").append(order.getDirection()));
        builder.queryParam("sort", sb.toString());
        return builder.build().toUriString();
    }

    private <F> String appendQueryStringFromFilters(String urlString, F filters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlString);
        try {
            for (Field f : filters.getClass().getDeclaredFields()) {
                boolean isAccessible = f.isAccessible();
                f.setAccessible(true);
                if (f.get(filters) instanceof String) {
                    builder.queryParam(f.getName(), f.get(filters));
                }
                f.setAccessible(isAccessible);
            }
        } catch (Exception e) {
            //todo
        }
        return builder.build().toUriString();
    }

    public <R, ID> R get(String url, ID identifier, Class<R> clazz) {
        try {
            ResponseEntity<R> responseEntity = rest.getForEntity(url, clazz, identifier);
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                //TODO
                //throw exception
            }
            return responseEntity.getBody();
        } catch (Exception e) {
            //TODO
            return null;
        }
    }

    public <R> R create(String url, R resourceToCreate, Class<R> clazz) {
        try {
            ResponseEntity<R> responseEntity = rest.postForEntity(url, resourceToCreate, clazz);
            if (!HttpStatus.CREATED.equals(responseEntity.getStatusCode())) {
                //TODO
                //throw exception
            }
            return responseEntity.getBody();
        } catch (Exception e) {
            //TODO
            return null;
        }
    }

    public <R, ID> R modify(String url, ID identifier, R resourceToModify, Class<R> clazz) {
        try {
            ResponseEntity<R> responseEntity = rest.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(resourceToModify),
                    clazz,
                    identifier
            );
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                //TODO
                //throw exception
            }
            return responseEntity.getBody();
        } catch (Exception e) {
            //TODO
            return null;
        }
    }

    public <ID> void delete(String url, ID identifier) {
        try {
            rest.delete(url, identifier);
        } catch (Exception e) {
            //TODO
        }
    }

    public <I, O> O genericPost(String url, I resource, Class<O> clazz) {
        try {
            ResponseEntity<O> responseEntity = rest.postForEntity(url, resource, clazz);
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                //TODO
                //throw exception
            }
            return responseEntity.getBody();
        } catch (Exception e) {
            //TODO
            return null;
        }
    }
}
