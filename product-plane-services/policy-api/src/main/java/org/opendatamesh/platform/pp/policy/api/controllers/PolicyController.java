package org.opendatamesh.platform.pp.policy.api.controllers;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyController {
    Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions);

    PolicyResource getPolicy(Long id);

    PolicyResource getPolicyVersion(Long versionId);

    PolicyResource createPolicy(PolicyResource policy);

    PolicyResource modifyPolicy(Long id, PolicyResource policy);

    void deletePolicy(Long id);

}
