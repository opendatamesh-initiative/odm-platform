package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/policies"
)
@Validated
@Tag(
        name = "Policies",
        description = "Endpoints associated to Policies"
)
public abstract class AbstractPolicyController {

    @GetMapping
    public abstract Page<PolicyResource> getPolicies(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicySearchOptions searchOptions
    );

    @GetMapping(value = "/{id}")
    public abstract PolicyResource getPolicy(
            @Parameter(description = "", required = true)
            @PathVariable(value = "id") Long id
    );

    @PostMapping
    public abstract PolicyResource createPolicy(
            @Parameter(description = "")
            @RequestBody PolicyResource policy
    );

    @PutMapping(value = "/{id}")
    public abstract PolicyResource modifyPolicy(
            @Parameter(description = "")
            @PathVariable(value = "id") Long id,
            @Parameter(description = "")
            @RequestBody PolicyResource policy
    );

    @DeleteMapping(value = "/{id}")
    public abstract PolicyResource deletePolicy(
            @Parameter(description = "")
            @PathVariable(value = "id") Long id
    );

}
