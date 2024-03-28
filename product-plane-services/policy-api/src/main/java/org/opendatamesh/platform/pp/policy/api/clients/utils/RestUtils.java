package org.opendatamesh.platform.pp.policy.api.clients.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;

public class RestUtils {

    private final TestRestTemplate rest;
    private final ObjectMapper objectMapper;

    public RestUtils(TestRestTemplate restTemplate) {
        this.rest = restTemplate;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <R, F> Page<R> getPage(String url, Pageable pageable, F filters, Class<R> clazz) {
        try {
            if (pageable != null) {
                url = appendQueryStringFromPageable(url, pageable);
            }
            if (filters != null) {
                url = appendQueryStringFromFilters(url, filters);
            }

            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    ObjectNode.class
            );
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            JavaType type = objectMapper.getTypeFactory().constructParametricType(Page.class, clazz);
            return objectMapper.treeToValue(responseEntity.getBody(), type);
        } catch (RestClientException e) {
            throw new InternalServerException();//TODO
        } catch (JsonProcessingException e) {
            throw new InternalServerException();//TODO
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
            throw new InternalServerException();//TODO
        }
        return builder.build().toUriString();
    }

    public <R, ID> R get(String url, ID identifier, Class<R> clazz) {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.getForEntity(url, ObjectNode.class, identifier);
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (RestClientException e) {
            throw new InternalServerException();//TODO
        } catch (JsonProcessingException e) {
            throw new InternalServerException();//TODO
        }
    }

    public <R> R create(String url, R resourceToCreate, Class<R> clazz) {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.postForEntity(url, resourceToCreate, ObjectNode.class);
            if (!HttpStatus.CREATED.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (RestClientException e) {
            throw new InternalServerException();//TODO
        } catch (JsonProcessingException e) {
            throw new InternalServerException();//TODO
        }
    }

    private <R> void throwServiceExceptionByResourceType(Class<R> clazz, ObjectMapper objectMapper, ResponseEntity<ObjectNode> responseEntity) throws JsonProcessingException {
        ErrorRes errorRes = objectMapper.treeToValue(responseEntity.getBody(), ErrorRes.class);
        PolicyApiStandardErrors apiStandardErrors = PolicyApiStandardErrors.getByCode(errorRes.getCode());
        throw new InternalServerException(apiStandardErrors, errorRes.getMessage());//TODO
    }

    public <R, ID> R modify(String url, ID identifier, R resourceToModify, Class<R> clazz) {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(resourceToModify),
                    ObjectNode.class,
                    identifier
            );
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (RestClientException e) {
            throw new InternalServerException();//TODO
        } catch (JsonProcessingException e) {
            throw new InternalServerException();//TODO
        }
    }

    public <ID> void delete(String url, ID identifier) {
        try {
            rest.delete(url, identifier);
        } catch (RestClientException e) {
            throw new InternalServerException();//TODO
        }
    }

    public <I, O> O genericPost(String url, I resource, Class<O> clazz) {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.postForEntity(url, resource, ObjectNode.class);
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (RestClientException e) {
            throw new InternalServerException();//TODO
        } catch (JsonProcessingException e) {
            throw new InternalServerException();//TODO
        }
    }
}
