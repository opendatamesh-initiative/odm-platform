package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.server.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolicyController extends AbstractPolicyController {
    @Autowired
    private PolicyService service;

    @Override
    public Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions) {
        return service.findAllResourcesFiltered(pageable, searchOptions);
    }

    @Override
    public PolicyResource getPolicy(Long rootId) {
        return service.findOnePolicyResource(rootId);
    }

    @Override
    public PolicyResource createPolicy(PolicyResource policy) {
        return service.createPolicyResource(policy);
    }

    @Override
    public PolicyResource modifyPolicy(Long rootId, PolicyResource policy) {
        return service.overwritePolicyResource(rootId, policy);
    }

    @Override
    public void deletePolicy(Long rootId) {
        service.deletePolicy(rootId);
    }

    @Override
    public PolicyResource getPolicyVersion(Long versionId) {
        return service.findOneResource(versionId);
    }

}
