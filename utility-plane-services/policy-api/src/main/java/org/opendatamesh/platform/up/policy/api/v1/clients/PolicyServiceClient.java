package org.opendatamesh.platform.up.policy.api.v1.clients;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.opendatamesh.platform.up.policy.api.v1.resources.PolicyResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Data
@NoArgsConstructor
public class PolicyServiceClient {

    String address;

    RestTemplate restTemplate;

    public PolicyServiceClient(String address) {
        this.address = Objects.requireNonNull(address);
        restTemplate = new RestTemplate();
    }

    // ----------------------------------------
    // POLICY endpoints
    // ----------------------------------------

    public PolicyResource[] readPolicies() {

        ResponseEntity<PolicyResource[]> getResponse = restTemplate.getForEntity(
                apiUrl(Routes.POLICIES),
                PolicyResource[].class
        );

        return getResponse.getBody();

    }

    public PolicyResource readOnePolicy(String id) {

        ResponseEntity<PolicyResource> getResponse = restTemplate.getForEntity(
                apiUrlOfItem(Routes.POLICIES),
                PolicyResource.class,
                id
        );

        return getResponse.getBody();

    }


    // ----------------------------------------
    // SUITE endpoint
    // ----------------------------------------

    // TODO ...


    // ----------------------------------------
    // VALIDATION endpoint
    // ----------------------------------------

    // TODO ...


    // ----------------------------------------
    // Utils
    // ----------------------------------------

    protected String apiUrl(Routes route) {
        return apiUrl(route, "");
    }

    protected String apiUrl(Routes route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    protected String apiUrlOfItem(Routes route) {
        return apiUrl(route, "/{id}");
    }

    protected String apiUrlFromString(String routeUrlString) {
        return address + routeUrlString;
    }


    // ----------------------------------------
    // Routes
    // ----------------------------------------

    private enum Routes {

        POLICIES("/api/v1/planes/utility/policy-services/opa/policies");

        private final String path;

        Routes(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return this.path;
        }

        public String getPath() {
            return path;
        }
    }

}