package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicySelectorService {

    @Autowired
    PolicyService policyService;

    public List<Policy> selectPoliciesBySuite(PolicyEvaluationRequestResource.EventType suite) {

        PolicySearchOptions policySearchOptions = new PolicySearchOptions();
        policySearchOptions.setSuite(suite.toString());

        return policyService.findAllFilteredList(policySearchOptions);

    }

}
