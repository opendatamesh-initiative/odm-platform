package org.opendatamesh.platform.core.commons.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
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

    public TestRestTemplate rest;
    protected MockRestServiceServer mockServer;
    protected ClientHttpRequestFactory clientRequestFactory;


    protected ObjectMapper mapper;

    public ODMClient(String serverAddress, ObjectMapper mapper) {
        this(serverAddress, new RestTemplate(), mapper);
    }

    public ODMClient(String serverAddress, RestTemplate restTemplate, ObjectMapper mapper) {
        this.serverAddress = serverAddress;
        this.mapper = mapper;
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder.configure(restTemplate);
        rest = initRestTemplate( new TestRestTemplate(restTemplateBuilder) );    
        acceptMediaType = MediaType.APPLICATION_JSON;
        contentMediaType = MediaType.APPLICATION_JSON;
    }

    private TestRestTemplate initRestTemplate(TestRestTemplate restTemplate) {

        initRestTemplateMessageConverter(restTemplate);
        initRestTemplateRequestFactory(restTemplate);
        initRestTemplateUriTemplateBuilder(restTemplate);
        
        restTemplate.getRestTemplate().setErrorHandler(new ODMRestTemplateErrorHandler());

        return restTemplate;
    }

    // WARNING this method at the moment does nothing
    // TODO use it to setup DPDSPareser as converter for DPDSDataProductVersion
    private void initRestTemplateMessageConverter(TestRestTemplate restTemplate) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        List<HttpMessageConverter<?>>  converters = restTemplate.getRestTemplate().getMessageConverters();
        for(HttpMessageConverter c : converters) {

            if(c instanceof MappingJackson2HttpMessageConverter) {
               MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter)c;
               Set<Object> modules = m.getObjectMapper().getRegisteredModuleIds();
            }
        }
        //.add(converter);
    }

    private void initRestTemplateRequestFactory(TestRestTemplate restTemplate) {
        // TODO this is ok for debugging IT not for executing them while building. Link the behaviour to application context (i.e. debug=>timeout didabled timeout while test=>timeout active)
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        // requestFactory.setConnectTimeout(30000);
        // requestFactory.setReadTimeout(30000);
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);
    }

    private void initRestTemplateUriTemplateBuilder(TestRestTemplate restTemplate) {
        // add uri template handler because '+' of iso date would not be encoded
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
    }

    public void bindMockServer() {
        RestTemplate restTemplate = rest.getRestTemplate();
        clientRequestFactory = restTemplate.getRequestFactory();
        mockServer = MockRestServiceServer.bindTo(restTemplate)
                .ignoreExpectOrder(true)
                .build();
    }

    public void unbindMockServer() {
        RestTemplate restTemplate = rest.getRestTemplate();
        restTemplate.setRequestFactory(clientRequestFactory);
    }

    public void resetMockServer() {
        if(mockServer != null && clientRequestFactory != null) {
            unbindMockServer();
            bindMockServer();
        }
        
    }

    
    public String apiUrlOfItem(ODMApiRoutes route) {
        return apiUrl(route, "/{id}", null);
    }

    public String apiUrlOfItem(ODMApiRoutes route, Map<String, Object> queryParams) {
        return apiUrl(route, "/{id}", queryParams);
    }

    public String apiUrl(ODMApiRoutes route) {
        return apiUrl(route, "", null);
    }

    public String apiUrl(ODMApiRoutes route, Map<String, Object> queryParams) {
        return apiUrl(route, "", queryParams);
    }

    public String apiUrl(ODMApiRoutes route, String extension) {
        return apiUrl(route, extension, null);
    }

    public String apiUrl(ODMApiRoutes route, String extension, Map<String, Object> queryParams) {
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

    protected String apiUrlWithQueryParams(ODMApiRoutes route, List<String> params) {
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

    // becaus restTemplate does not have such method for patch 
    public <T> ResponseEntity<T> patchForEntity(String url, @Nullable Object request,
			Class<T> responseType, Object... uriVariables) throws RestClientException {

		RequestCallback requestCallback = rest.getRestTemplate().httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = rest.getRestTemplate().responseEntityExtractor(responseType);
		ResponseEntity<T> result = rest.getRestTemplate().execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
        Assert.state(result != null, "No result");
        return result;
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
