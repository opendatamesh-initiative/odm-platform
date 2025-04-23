package org.opendatamesh.platform.pp.notification.server.services.utils.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.exceptions.ClientException;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.http.HttpEntity;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.http.HttpHeader;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.http.HttpMethod;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.jackson.PageUtility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Utility class for performing REST operations and mapping JSON responses to Java objects.
 * <p>
 * This class encapsulates various methods to execute HTTP requests (GET, POST, PUT, PATCH, DELETE)
 * using a {@link RestUtilsTemplate} and convert the JSON responses into Java objects using an {@link ObjectMapper}.
 * It also provides helper methods to support pagination and file download operations.
 * </p>
 */
class BaseRestUtils implements RestUtils {

    protected final RestUtilsTemplate rest;
    protected final ObjectMapper objectMapper;

    public BaseRestUtils(RestUtilsTemplate restUtilsTemplate) {
        this.rest = restUtilsTemplate;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule simpleModule = new SimpleModule()
                .addAbstractTypeMapping(Page.class, PageUtility.class);
        objectMapper.registerModule(simpleModule);
    }

    /**
     * Retrieves a paginated response from a REST API endpoint and maps it to a {@link Page} of the specified type.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param pageable    the pagination information to include in the request.
     * @param filters     the filters to apply to the request.
     * @param clazz       the target class to which the response should be mapped.
     * @param <R>         the type of elements in the returned page.
     * @param <F>         the type of the filters.
     * @return a {@link Page} of mapped objects of type {@code R}.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    @Override
    public <R, F> Page<R> getPage(String url, List<HttpHeader> httpHeaders, Pageable pageable, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            UriComponents uriComponents = new UriComponents(url);
            if (filters != null) {
                buildUriComponentFromFilters(uriComponents, filters);
            }
            if (pageable != null) {
                buildUriComponentFromPageable(uriComponents, pageable);
            }

            JsonNode response = rest.exchange(
                    uriComponents.getBuilder().build().toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    JsonNode.class,
                    uriComponents.getUriVariables()
            );

            JavaType type = objectMapper.getTypeFactory().constructParametricType(Page.class, clazz);
            return objectMapper.treeToValue(response, type);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Executes a generic HTTP GET request with optional filters and maps the response to an object of the specified type.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param filters     the filters to apply to the request.
     * @param clazz       the target class to which the response should be mapped.
     * @param <R>         the type of the returned object.
     * @param <F>         the type of the filters.
     * @return an object of type {@code R} representing the response.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    @Override
    public <R, F> R genericGet(String url, List<HttpHeader> httpHeaders, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            UriComponents uriComponents = new UriComponents(url);
            if (filters != null) {
                buildUriComponentFromFilters(uriComponents, filters);
            }

            JsonNode response = rest.exchange(
                    uriComponents.getBuilder().build().toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    JsonNode.class,
                    uriComponents.getUriVariables()
            );

            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves a resource identified by a given identifier from the specified URL.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param identifier  the identifier of the resource.
     * @param clazz       the target class to which the response should be mapped.
     * @param <R>         the type of the returned object.
     * @param <ID>        the type of the identifier.
     * @return an object of type {@code R} representing the retrieved resource.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    @Override
    public <R, ID> R get(String url, List<HttpHeader> httpHeaders, ID identifier, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    JsonNode.class,
                    identifier
            );

            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Creates a new resource by sending a POST request to the specified URL.
     *
     * @param url              the base URL of the REST endpoint.
     * @param httpHeaders      the HTTP headers to include in the request.
     * @param resourceToCreate the resource object to be created.
     * @param clazz            the target class to which the response should be mapped.
     * @param <R>              the type of the resource.
     * @return the created resource mapped to an object of type {@code R}.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    @Override
    public <R> R create(String url, List<HttpHeader> httpHeaders, R resourceToCreate, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(resourceToCreate, httpHeaders),
                    JsonNode.class
            );
            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Updates an existing resource by sending a PUT request to the specified URL.
     *
     * @param url              the base URL of the REST endpoint.
     * @param httpHeaders      the HTTP headers to include in the request.
     * @param identifier       the identifier of the resource to update.
     * @param resourceToModify the resource object containing updated data.
     * @param clazz            the target class to which the response should be mapped.
     * @param <R>              the type of the resource.
     * @param <ID>             the type of the identifier.
     * @return the updated resource mapped to an object of type {@code R}.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    @Override
    public <R, ID> R put(String url, List<HttpHeader> httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(resourceToModify, httpHeaders),
                    JsonNode.class,
                    identifier
            );

            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Partially updates a resource by sending a PATCH request to the specified URL.
     *
     * @param url              the base URL of the REST endpoint.
     * @param httpHeaders      the HTTP headers to include in the request.
     * @param identifier       the identifier of the resource to update.
     * @param resourceToModify the resource object containing the partial updates.
     * @param clazz            the target class to which the response should be mapped.
     * @param <R>              the type of the resource.
     * @param <ID>             the type of the identifier.
     * @return the updated resource mapped to an object of type {@code R}.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    @Override
    public <R, ID> R patch(String url, List<HttpHeader> httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.PATCH,
                    new HttpEntity<>(resourceToModify, httpHeaders),
                    JsonNode.class,
                    identifier
            );

            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Deletes a resource identified by the given identifier by sending a DELETE request to the specified URL.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param identifier  the identifier of the resource to delete.
     * @param <ID>        the type of the identifier.
     * @throws ClientException if an HTTP client error occurs.
     */
    @Override
    public <ID> void delete(String url, List<HttpHeader> httpHeaders, ID identifier) throws ClientException {
        rest.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(httpHeaders),
                JsonNode.class,
                identifier
        );
    }

    /**
     * Executes a generic HTTP POST request with the given resource and maps the response to an object of the specified type.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param resource    the resource object to send in the POST request.
     * @param clazz       the target class to which the response should be mapped.
     * @param <I>         the type of the resource sent in the request.
     * @param <O>         the type of the object returned.
     * @return an object of type {@code O} representing the response.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    @Override
    public <I, O> O genericPost(String url, List<HttpHeader> httpHeaders, I resource, Class<O> clazz) throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(resource, httpHeaders),
                    JsonNode.class
            );

            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Downloads a file from a REST API endpoint and saves it to the specified location.
     *
     * @param url           The URL of the API endpoint to download the file from.
     * @param httpHeaders   The HTTP headers to include in the request (e.g., authorization headers). Can be {@code null}.
     * @param resource      The request body to send with the download request. Can be {@code null} if no request body is needed (e.g., for simple GET downloads).
     * @param storeLocation The File object representing the location where the downloaded file should be saved.
     * @return The File object representing the saved file.
     * @throws ClientException                If a client-side error occurs during the API call (e.g., HTTP error codes).
     * @throws ClientResourceMappingException If an error occurs during file processing (e.g., I/O exceptions).
     * @throws NullPointerException           if storeLocation is null.
     */
    @Override
    public File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation)
            throws ClientException, ClientResourceMappingException {
        return rest.download(url, httpHeaders, resource, storeLocation);
    }

    /**
     * Adds pagination query parameters to the provided {@link UriComponents} based on the {@link Pageable} object.
     *
     * @param uriComponents the URI components object to which pagination parameters will be added.
     * @param pageable      the pageable object containing page number, size, and sort information.
     */
    private void buildUriComponentFromPageable(UriComponents uriComponents, Pageable pageable) {
        uriComponents.getBuilder().queryParam("page", "{page}");
        uriComponents.getUriVariables().put("page", pageable.getPageNumber());

        uriComponents.getBuilder().queryParam("size", "{size}");
        uriComponents.getUriVariables().put("size", pageable.getPageSize());

        StringBuilder sortBuilder = new StringBuilder();
        pageable.getSort().forEach(order -> {
            if (sortBuilder.length() > 0) {
                sortBuilder.append(",");
            }
            sortBuilder.append(order.getProperty()).append(",").append(order.getDirection());
        });
        uriComponents.getBuilder().queryParam("sort", "{sort}");
        uriComponents.getUriVariables().put("sort", sortBuilder.toString());
    }

    /**
     * Adds query parameters for filters to the provided {@link UriComponents} based on the fields of the filter object.
     * <p>
     * This method iterates over the declared fields of the filter object, making them accessible to extract their values,
     * and adds them as query parameters. For collections or arrays, it creates placeholders for each element.
     * </p>
     *
     * @param uriComponents the URI components object to which filter parameters will be added.
     * @param filters       the filter object containing the fields to be used as query parameters.
     * @param <F>           the type of the filter object.
     * @throws OdmPlatformInternalServerException if reflection fails or an error occurs during parameter construction.
     */
    private <F> void buildUriComponentFromFilters(UriComponents uriComponents, F filters) {
        try {
            for (Field f : filters.getClass().getDeclaredFields()) {
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                Object value = f.get(filters);
                String paramName = f.getName();

                if (value instanceof Collection) {
                    Collection<?> collection = (Collection<?>) value;
                    List<String> placeholders = new ArrayList<>();
                    int index = 0;
                    for (Object item : collection) {
                        String placeholder = paramName + index;
                        placeholders.add("{" + placeholder + "}");
                        uriComponents.getUriVariables().put(placeholder, item);
                        index++;
                    }
                    uriComponents.getBuilder().queryParam(paramName, placeholders.toArray());
                } else if (value != null && value.getClass().isArray()) {
                    int length = Array.getLength(value);
                    List<String> placeholders = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        Object item = Array.get(value, i);
                        String placeholder = paramName + i;
                        placeholders.add("{" + placeholder + "}");
                        uriComponents.getUriVariables().put(placeholder, item);
                    }
                    uriComponents.getBuilder().queryParam(paramName, placeholders.toArray());
                } else {
                    uriComponents.getBuilder().queryParam(paramName, "{" + paramName + "}");
                    uriComponents.getUriVariables().put(paramName, value);
                }
                f.setAccessible(accessible);
            }
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    /**
     * Utility class for building URI components with query parameters and URI variables.
     */
    private static class UriComponents {
        private final UriComponentsBuilder builder;
        private final Map<String, Object> uriVariables;

        public UriComponents(String baseUrl) {
            this.uriVariables = new HashMap<>();
            this.builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        }

        public UriComponentsBuilder getBuilder() {
            return builder;
        }

        public Map<String, Object> getUriVariables() {
            return uriVariables;
        }
    }

}
