package org.opendatamesh.platform.pp.policy.server.controllers;

import org.opendatamesh.platform.pp.policy.api.controllers.AbstractPolicyEvaluationResultController;
import org.opendatamesh.platform.pp.policy.api.controllers.PolicyEvaluationResultController;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.opendatamesh.platform.pp.policy.server.services.PolicyEvaluationResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolicyEvaluationResultControllerImpl extends AbstractPolicyEvaluationResultController {

    @Autowired
    private PolicyEvaluationResultService service;

    public Page<PolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions) {
        return service.findAllResourcesFiltered(pageable, searchOptions);
    }

    public PolicyEvaluationResultResource getPolicyEvaluationResult(Long id) {
        return service.findOneResource(id);
    }

    public PolicyEvaluationResultResource createPolicyEvaluationResult(PolicyEvaluationResultResource policyEvaluationResult) {
        return service.createResource(policyEvaluationResult);
    }

    public PolicyEvaluationResultResource modifyPolicyEvaluationResult(Long id, PolicyEvaluationResultResource policyEvaluationResult) {
        return service.overwriteResource(id, policyEvaluationResult);
    }

    public void deletePolicyEvaluationResult(Long id) {
        service.delete(id);
    }

}
