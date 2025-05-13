package org.opendatamesh.platform.pp.policy.server.client.utils;


import org.opendatamesh.platform.pp.policy.server.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.pp.policy.server.client.utils.http.HttpEntity;
import org.opendatamesh.platform.pp.policy.server.client.utils.http.HttpHeader;
import org.opendatamesh.platform.pp.policy.server.client.utils.http.HttpMethod;

import java.io.File;
import java.util.List;
import java.util.Map;

interface RestUtilsTemplate {

    <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws ClientException;

    <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws ClientException;

    File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation) throws ClientException;

}
