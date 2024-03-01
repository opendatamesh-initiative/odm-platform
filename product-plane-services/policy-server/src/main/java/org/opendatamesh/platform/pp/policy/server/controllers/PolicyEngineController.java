package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyEngineController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.opendatamesh.platform.pp.policy.server.services.PolicyEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolicyEngineController extends AbstractPolicyEngineController {

    @Autowired
    private PolicyEngineService service;

    @Override
    public Page<PolicyEngineResource> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions) {
        return service.findAllResourcesFiltered(pageable, searchOptions);
    }

    @Override
    public PolicyEngineResource getPolicyEngine(Long id) {
        return service.findOneResource(id);
    }

    @Override
    public PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngine) {
        return service.createResource(policyEngine);
    }

    @Override
    public PolicyEngineResource modifyPolicyEngine(Long id, PolicyEngineResource policyEngine) {
        return service.overwriteResource(id, policyEngine);
    }

    @Override
    public PolicyEngineResource deletePolicyEngine(Long id) {
        return service.deleteReturningResource(id);
    }
}
