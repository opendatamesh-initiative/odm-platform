package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.server.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

@Controller
public class PolicyController extends AbstractPolicyController {
    @Autowired
    private PolicyService service;

    @Override
    public Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions) {
        return service.findAllResourcesFiltered(pageable, searchOptions);
    }

    @Override
    public PolicyResource getPolicy(Long id) {
        return service.findOneResource(id);
    }

    @Override
    public PolicyResource createPolicy(PolicyResource policy) {
        return service.createResource(policy);
    }

    @Override
    public PolicyResource modifyPolicy(Long id, PolicyResource policy) {
        return service.overwriteResource(id, policy);
    }

    @Override
    public PolicyResource deletePolicy(Long id) {
        return service.deleteReturningResource(id);
    }

}
