package org.opendatamesh.platform.up.policy.api.v1.clients;

import org.opendatamesh.platform.pp.registry.api.v1.clients.ODMClient;
import org.opendatamesh.platform.pp.registry.api.v1.clients.Routes;
import org.opendatamesh.platform.up.policy.api.v1.resources.PolicyResource;
import org.springframework.http.ResponseEntity;

public class PolicyServiceClient extends ODMClient {

    public PolicyServiceClient(String serverAddress) {
        super(serverAddress);
    }

    // ----------------------------------------
    // POLICY endpoints
    // ----------------------------------------

    public PolicyResource[] readPolicies() {

        ResponseEntity<PolicyResource[]> getResponse = rest.getForEntity(
                apiUrl(Routes.POLICYSERVICE_POLICY),
                PolicyResource[].class
        );

        return getResponse.getBody();

    }

    public PolicyResource readOnePolicy(String id) {

        ResponseEntity<PolicyResource> getResponse = rest.getForEntity(
                apiUrlOfItem(Routes.POLICYSERVICE_POLICY),
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


}