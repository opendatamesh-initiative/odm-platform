package org.opendatamesh.platform.pp.notification.server.services.utils.rest;


import org.opendatamesh.platform.pp.notification.server.services.utils.rest.exceptions.ClientException;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.http.HttpEntity;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.http.HttpHeader;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.http.HttpMethod;

import java.io.File;
import java.util.List;
import java.util.Map;

interface RestUtilsTemplate {

    <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws ClientException;

    <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws ClientException;

    File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation) throws ClientException;

}
