package org.opendatamesh.platform.core.commons.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ODMClient {
    
    protected String serverAddress;

    protected MediaType acceptMediaType;

    protected MediaType contentMediaType;

    protected TestRestTemplate rest;

    protected ObjectMapper mapper;


    public ODMClient(String serverAddress, ObjectMapper mapper) {
        this.serverAddress = serverAddress;
        this.mapper = mapper;
        rest = initRestTemplate();
        acceptMediaType = MediaType.APPLICATION_JSON;
        contentMediaType = MediaType.APPLICATION_JSON;
    }

    private TestRestTemplate initRestTemplate() {

        TestRestTemplate restTemplate = new TestRestTemplate();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        List<HttpMessageConverter<?>>  converters = restTemplate.getRestTemplate().getMessageConverters();
        for(HttpMessageConverter c : converters) {

            if(c instanceof MappingJackson2HttpMessageConverter) {
               MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter)c;
              
               Set<Object> modules = m.getObjectMapper().getRegisteredModuleIds();
               System.out.println(modules);
            }
            System.out.println(c.getClass().getCanonicalName() + "\n" + c.getSupportedMediaTypes());
        }
        //.add(converter);
        //rest.setPort(port);
        
        // TODO this is ok for debugging IT not for executing them while building. Link the behaviour to application context (i.e. debug=>timeout didabled timeout while test=>timeout active)
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        // requestFactory.setConnectTimeout(30000);
        // requestFactory.setReadTimeout(30000);
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);
        
        // add uri template handler because '+' of iso date would not be encoded
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);

        return restTemplate;
    }
    
    protected String apiUrlOfItem(RoutesInterface route) {
        return apiUrl(route, "/{id}", null);
    }

    protected String apiUrlOfItem(RoutesInterface route, Map<String, Object> queryParams) {
        return apiUrl(route, "/{id}", queryParams);
    }

    protected String apiUrl(RoutesInterface route) {
        return apiUrl(route, "", null);
    }

    protected String apiUrl(RoutesInterface route, String extension) {
        return apiUrl(route, extension, null);
    }

    protected String apiUrl(RoutesInterface route, String extension, Map<String, Object> queryParams) {
        String urlTemplate = null;

        urlTemplate = (extension != null)?
            apiUrlFromString(route.getPath() + extension):
            apiUrlFromString(route.getPath());

        if(queryParams != null) {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(urlTemplate);
            for(String paramName : queryParams.keySet()){
                uriBuilder.queryParam(paramName, "{" + paramName + "}");
            }
            urlTemplate = uriBuilder.encode().toUriString();
        }
        
        return urlTemplate;
    }

    protected String apiUrlWithQueryParams(RoutesInterface route, List<String> params) {
        String ext = "";
        if(params.size() != 0) {
            ext = "?";
            for(int i=0; i<params.size();i++) {
                if(i!=0) ext += "&";
                ext += params.get(i);
            }
        }
        return apiUrl(route, ext);
    }

    protected String apiUrlFromString(String servicePath) {
        return serverAddress + servicePath;
    }

    protected HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(acceptMediaType));
        headers.setContentType(contentMediaType);
        return headers;
    }

    protected <T> HttpEntity<T> getHttpEntity(T payload) throws IOException {
        return new HttpEntity<T>(payload, getHeaders());
    }

    //this method maps the responses of the client based on the response's status code.
    //the mapper returns the passed acceptedClass if the response contains the acceptedStatusCode(s)
    //otherwise it returns the errorClass
    protected ResponseEntity mapResponseEntity(ResponseEntity response,
                                                    HttpStatus acceptedStatusCode,
                                                    Class acceptedClass,
                                                    Class errorClass) throws JsonProcessingException {
        return mapResponseEntity(response,List.of(acceptedStatusCode),acceptedClass,errorClass);
    }

    protected ResponseEntity mapResponseEntity(ResponseEntity response,
                                               List<HttpStatus> acceptedStatusCodes,
                                               Class acceptedClass,
                                               Class errorClass) throws JsonProcessingException {
        ResponseEntity result;
        if (acceptedStatusCodes.contains(response.getStatusCode())){
            result = ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(mapper.readValue(mapper.writeValueAsString(response.getBody())
                            , acceptedClass));
        }
        else {
            result = ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(mapper.readValue(mapper.writeValueAsString(response.getBody())
                            , errorClass));
        }
        return result;
    }
}
