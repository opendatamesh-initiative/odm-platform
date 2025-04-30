package org.opendatamesh.platform.pp.policy.server.client.utils;

import org.opendatamesh.platform.pp.policy.server.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.pp.policy.server.client.utils.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.pp.policy.server.client.utils.http.HttpHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface RestUtils {
    <R, F> Page<R> getPage(String url, List<HttpHeader> httpHeaders, Pageable pageable, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R, F> R genericGet(String url, List<HttpHeader> httpHeaders, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R, ID> R get(String url, List<HttpHeader> httpHeaders, ID identifier, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R> R create(String url, List<HttpHeader> httpHeaders, R resourceToCreate, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R, ID> R put(String url, List<HttpHeader> httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R, ID> R patch(String url, List<HttpHeader> httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <ID> void delete(String url, List<HttpHeader> httpHeaders, ID identifier) throws ClientException;

    <I, O> O genericPost(String url, List<HttpHeader> httpHeaders, I resource, Class<O> clazz) throws ClientException, ClientResourceMappingException;

    File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation)
            throws ClientException, ClientResourceMappingException;

    /**
     * Retrieves data from a REST API endpoint in pages and processes each page using the provided consumer.
     * This method handles pagination automatically, retrieving data in batches until all data is processed or the maximum number of elements is reached.
     *
     * @param <T>            The type of the data being retrieved.
     * @param retrieveMethod A function that takes a Pageable object and returns a Page of data. This function should encapsulate the logic for retrieving data from the API.
     * @param processMethod  A consumer that accepts a List of data and performs the desired processing on it. This is called for each retrieved page.
     * @param batchSize      The number of elements to retrieve in each page.
     * @param maxElements    The maximum number of elements to retrieve. If this limit is reached, the process stops even if there are more pages available.
     * @implNote This method uses a do-while loop to iterate through the pages. It is important that the {@code retrieveMethod} function correctly handles the {@code Pageable} object to ensure proper pagination.
     */
    static <T> void retrieveAndProcessPageable(
            Function<Pageable, Page<T>> retrieveMethod,
            Consumer<List<T>> processMethod,
            int batchSize, int maxElements
    ) {
        processPageable(pageable -> {
            Page<T> page = retrieveMethod.apply(pageable);
            processMethod.accept(page.toList());
            return page;
        }, batchSize, maxElements);
    }

    private static <T> void processPageable(Function<Pageable, Page<T>> operation, int batchSize, int maxElements) {
        Pageable pageRequest = PageRequest.of(0, batchSize);
        Page<T> page;
        do {
            page = operation.apply(pageRequest);
            pageRequest = pageRequest.next();
        } while (page.hasNext() && limitNotReachedYet(maxElements, batchSize, page.getNumber()));
    }

    private static boolean limitNotReachedYet(int maxElements, int batchSize, int pageNumber) {
        return (batchSize * pageNumber) <= maxElements;
    }

}
