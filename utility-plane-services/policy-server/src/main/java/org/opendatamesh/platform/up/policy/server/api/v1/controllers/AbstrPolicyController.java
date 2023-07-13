package org.opendatamesh.platform.up.policy.server.api.v1.controllers;

import org.opendatamesh.platform.up.policy.api.v1.controllers.AbstractPolicyController;
import org.opendatamesh.platform.up.policy.api.v1.errors.PolicyserviceOpaAPIStandardError;
import org.opendatamesh.platform.up.policy.api.v1.resources.PolicyResource;
import org.opendatamesh.platform.up.policy.server.api.v1.mappers.PolicyMapper;
import org.opendatamesh.platform.up.policy.server.database.entities.PolicyEntity;
import org.opendatamesh.platform.up.policy.server.database.repositories.PolicyRepository;
import org.opendatamesh.platform.up.policy.server.exceptions.NotFoundException;
import org.opendatamesh.platform.up.policy.server.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class AbstrPolicyController extends AbstractPolicyController {

    @Autowired
    protected PolicyService policyService;

    @Autowired
    protected PolicyRepository pr;

    @Autowired
    protected PolicyMapper pm;

    @Override
    public ResponseEntity readPolicies() {

        Iterable<PolicyEntity> policyEntities = pr.findAll();
        Iterable<PolicyResource> policies = pm.toResource(policyEntities);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(policies);

    }

    @Override
    public ResponseEntity readOnePolicy(String id) {

        if(!pr.existsById(id)) {
            throw new NotFoundException(
                    PolicyserviceOpaAPIStandardError.SC404_POLICY_NOT_FOUND,
                    "Policy " + id + " not found on DB"
            );
        }

        PolicyEntity policyEntity = pr.findById(id).get();
        PolicyResource policy = pm.toResource(policyEntity);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(policy);

    }

    @Override
    public abstract ResponseEntity createPolicy(PolicyResource policies);

    @Override
    public abstract ResponseEntity updatePolicy(String id, PolicyResource policies);

    @Override
    public abstract void deletePolicy(String id);

}
