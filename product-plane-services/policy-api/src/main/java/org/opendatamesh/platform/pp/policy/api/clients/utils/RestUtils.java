package org.opendatamesh.platform.pp.policy.api.clients.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
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
        SimpleModule simpleModule = new SimpleModule()
                .addAbstractTypeMapping(Page.class, PageUtility.class);
        objectMapper.registerModule(simpleModule);
    }

    public <R, F> Page<R> getPage(String url, Pageable pageable, F filters, Class<R> clazz) throws InternalServerException {
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
                    HttpEntity.EMPTY,
                    ObjectNode.class
            );
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            JavaType type = objectMapper.getTypeFactory().constructParametricType(Page.class, clazz);
            return objectMapper.treeToValue(responseEntity.getBody(), type);
        } catch (RestClientException | JsonProcessingException e) {
            throw new InternalServerException(e);
        }
    }

    public <R, ID> R get(String url, ID identifier, Class<R> clazz) throws InternalServerException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    ObjectNode.class,
                    identifier
            );
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (RestClientException | JsonProcessingException e) {
            throw new InternalServerException(e);
        }
    }

    public <R> R create(String url, R resourceToCreate, Class<R> clazz) throws InternalServerException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(resourceToCreate),
                    ObjectNode.class
            );
            if (!HttpStatus.CREATED.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (RestClientException | JsonProcessingException e) {
            throw new InternalServerException(e);
        }
    }

    public <R, ID> R put(String url, ID identifier, R resourceToModify, Class<R> clazz) throws InternalServerException {
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
        } catch (RestClientException | JsonProcessingException e) {
            throw new InternalServerException(e);
        }
    }

    public <R, ID> R patch(String url, ID identifier, R resourceToModify, Class<R> clazz) throws InternalServerException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.PATCH,
                    new HttpEntity<>(resourceToModify),
                    ObjectNode.class,
                    identifier
            );
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (RestClientException | JsonProcessingException e) {
            throw new InternalServerException(e);
        }
    }

    public <ID> void delete(String url, ID identifier) throws InternalServerException {
        try {
            rest.exchange(
                    url,
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    ObjectNode.class,
                    identifier
            );
        } catch (RestClientException e) {
            throw new InternalServerException(e);
        }
    }

    public <I, O> O genericPost(String url, I resource, Class<O> clazz) throws InternalServerException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(resource),
                    ObjectNode.class
            );
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                throwServiceExceptionByResourceType(clazz, objectMapper, responseEntity);
            }
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (RestClientException | JsonProcessingException e) {
            throw new InternalServerException(e);
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
            throw new InternalServerException(e);
        }
        return builder.build().toUriString();
    }

    private <R> void throwServiceExceptionByResourceType(Class<R> clazz, ObjectMapper objectMapper, ResponseEntity<ObjectNode> responseEntity) throws JsonProcessingException, InternalServerException {
        //TODO add mapping here?
        ErrorRes errorRes = objectMapper.treeToValue(responseEntity.getBody(), ErrorRes.class);
        throw new InternalServerException(errorRes.getMessage());
    }

}
