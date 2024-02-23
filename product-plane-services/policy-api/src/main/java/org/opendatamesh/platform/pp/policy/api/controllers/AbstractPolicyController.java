package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(
        value = "/policies"
)
public abstract class AbstractPolicyController {

    @GetMapping
    public abstract Page<PolicyResource> getPolicies(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicySearchOptions searchOptions
    );

    @GetMapping(value = "/{uuid}")
    public abstract PolicyResource getPolicy(
            @Parameter(description = "", required = true)
            @PathVariable(value = "uuid") String uuid
    );

    @PostMapping
    public abstract PolicyResource createPolicy(
            @Parameter(description = "")
            @RequestBody PolicyResource policy
    );

    @PutMapping(value = "/{uuid}")
    public abstract PolicyResource modifyPolicy(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid,
            @Parameter(description = "")
            @RequestBody PolicyResource policy
    );

    @DeleteMapping(value = "/{uuid}")
    public abstract PolicyResource deletePolicy(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid
    );

    @PostMapping(value = "/*/validate-object")
    public abstract PolicyEvaluationResultResource validateObject(
            @Parameter(description = "")
            @RequestBody EventResource eventResource
    );

}
