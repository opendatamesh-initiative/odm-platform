package org.opendatamesh.platform.pp.policy.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;

public class PolicyClientImpl implements PolicyClient {

    private final ODMClient odmClient;

    public PolicyClientImpl(String serverAddress, ObjectMapper mapper) {
        odmClient = new ODMClient(serverAddress, mapper);
    }

    public PolicyClientImpl(String serverAddress, RestTemplate restTemplate, ObjectMapper mapper) {
        odmClient = new ODMClient(serverAddress, restTemplate, mapper);
    }

    public Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions) {
        return getPage(odmClient.apiUrl(PolicyAPIRoutes.POLICIES), pageable, searchOptions);
    }

    public PolicyResource getPolicy(Long id) {
        return get(odmClient.apiUrl(PolicyAPIRoutes.POLICIES), id, PolicyResource.class);
    }

    public PolicyResource getPolicyVersion(Long versionId) {
        //TODO
        return get(
                odmClient.apiUrl(PolicyAPIRoutes.POLICIES),
                versionId,
                PolicyResource.class
        );
    }

    public PolicyResource createPolicy(PolicyResource policy) {
        return create(policy, odmClient.apiUrl(PolicyAPIRoutes.POLICIES), PolicyResource.class);
    }

    public PolicyResource modifyPolicy(Long id, PolicyResource policy) {
        return modify(policy, id, odmClient.apiUrl(PolicyAPIRoutes.POLICIES), PolicyResource.class);
    }

    public void deletePolicy(Long id) {
        delete(id, odmClient.apiUrl(PolicyAPIRoutes.POLICIES));
    }

    public Page<PolicyEngineResource> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions) {
        return getPage(odmClient.apiUrl(PolicyAPIRoutes.ENGINES), pageable, searchOptions);
    }

    public PolicyEngineResource getPolicyEngine(Long id) {
        return get(odmClient.apiUrl(PolicyAPIRoutes.ENGINES), id, PolicyEngineResource.class);
    }

    public PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngineResource) {
        return create(policyEngineResource, odmClient.apiUrl(PolicyAPIRoutes.ENGINES), PolicyEngineResource.class);
    }

    public PolicyEngineResource modifyPolicyEngine(Long id, PolicyEngineResource policyEngine) {
        return modify(policyEngine, id, odmClient.apiUrl(PolicyAPIRoutes.ENGINES), PolicyEngineResource.class);
    }

    public void deletePolicyEngine(Long id) {
        delete(id, odmClient.apiUrl(PolicyAPIRoutes.ENGINES));
    }


    public Page<PolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions) {
        return getPage(odmClient.apiUrl(PolicyAPIRoutes.RESULTS), pageable, searchOptions);
    }

    public PolicyEvaluationResultResource getPolicyEvaluationResult(Long id) {
        return get(odmClient.apiUrl(PolicyAPIRoutes.RESULTS), id, PolicyEvaluationResultResource.class);
    }

    public PolicyEvaluationResultResource createPolicyEvaluationResult(PolicyEvaluationResultResource policyEvaluationResult) {
        return create(policyEvaluationResult, odmClient.apiUrl(PolicyAPIRoutes.RESULTS), PolicyEvaluationResultResource.class);
    }

    public PolicyEvaluationResultResource modifyPolicyEvaluationResult(Long id, PolicyEvaluationResultResource policyEvaluationResult) {
        return modify(policyEvaluationResult, id, odmClient.apiUrl(PolicyAPIRoutes.RESULTS), PolicyEvaluationResultResource.class);
    }

    public void deletePolicyEvaluationResult(Long id) {
        delete(id, odmClient.apiUrl(PolicyAPIRoutes.RESULTS));
    }

    public PolicyEvaluationResultResource validateObject(EventResource eventResource) {
        return genericPost(eventResource, odmClient.apiUrl(PolicyAPIRoutes.VALIDATION), PolicyEvaluationResultResource.class);
    }

    //TODO move this methods outside ====================================================================================

    private <R, F> Page<R> getPage(String url, Pageable pageable, F filters) {
        try {
            ParameterizedTypeReference<Page<R>> responseType = new ParameterizedTypeReference<>() {
            };

            if (pageable != null) {
                url = appendQueryStringFromPageable(url, pageable);
            }
            if (filters != null) {
                url = appendQueryStringFromFilters(url, filters);
            }

            ResponseEntity<Page<R>> responseEntity = odmClient.rest.exchange(
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
                f.setAccessible(true);//TODO change this
                if (f.get(filters) instanceof String) {
                    builder.queryParam(f.getName(), f.get(filters));
                }
            }
        } catch (Exception e) {
            //todo
        }
        return builder.build().toUriString();
    }

    private <R, ID> R get(String url, ID identifier, Class<R> clazz) {
        try {
            ResponseEntity<R> responseEntity = odmClient.rest.getForEntity(url, clazz, identifier);
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

    private <R> R create(R resourceToCreate, String url, Class<R> clazz) {
        try {
            ResponseEntity<R> responseEntity = odmClient.rest.postForEntity(url, resourceToCreate, clazz);
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

    private <R, ID> R modify(R resourceToModify, ID identifier, String url, Class<R> clazz) {
        try {
            ResponseEntity<R> responseEntity = odmClient.rest.exchange(
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

    private <ID> void delete(ID identifier, String url) {
        try {
            odmClient.rest.delete(url, identifier);
        } catch (Exception e) {
            //TODO
        }
    }

    private <I, O> O genericPost(I resource, String url, Class<O> clazz) {
        try {
            ResponseEntity<O> responseEntity = odmClient.rest.postForEntity(url, resource, clazz);
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
