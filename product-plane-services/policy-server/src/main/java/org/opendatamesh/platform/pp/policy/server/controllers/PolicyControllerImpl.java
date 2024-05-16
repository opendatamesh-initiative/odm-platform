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
public class PolicyControllerImpl extends AbstractPolicyController {
    @Autowired
    private PolicyService service;

    public Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions) {
        return service.findAllResourcesFiltered(pageable, searchOptions);
    }

    public PolicyResource getPolicy(Long rootId) {
        return service.findOneResource(rootId);
    }

    public PolicyResource createPolicy(PolicyResource policy) {
        return service.createResource(policy);
    }

    public PolicyResource modifyPolicy(Long rootId, PolicyResource policy) {
        return service.overwriteResource(rootId, policy);
    }

    public void deletePolicy(Long rootId) {
        service.logicalDelete(rootId);
    }
    
    public PolicyResource getPolicyVersion(Long versionId) {
        return service.findPolicyResourceVersion(versionId);
    }

}
