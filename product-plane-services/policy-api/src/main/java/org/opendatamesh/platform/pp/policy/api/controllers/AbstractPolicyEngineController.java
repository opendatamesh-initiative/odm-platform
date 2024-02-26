package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/policy-engines"
)
@Validated
@Tag(
        name = "Policy Engines",
        description = "Endpoints associated to Policy Engines"
)
public abstract class AbstractPolicyEngineController {

    @GetMapping
    public abstract Page<PolicyEngineResource> getPolicyEngines(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicyEngineSearchOptions searchOptions
    );

    @GetMapping(value = "/{uuid}")
    public abstract PolicyEngineResource getPolicyEngine(
            @Parameter(description = "", required = true)
            @PathVariable(value = "uuid") String uuid
    );

    @PostMapping
    public abstract PolicyEngineResource createPolicyEngine(
            @Parameter(description = "")
            @RequestBody PolicyEngineResource policyEngine
    );

    @PutMapping(value = "/{uuid}")
    public abstract PolicyEngineResource modifyPolicyEngine(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid,
            @Parameter(description = "")
            @RequestBody PolicyEngineResource policyEngine
    );

    @DeleteMapping(value = "/{uuid}")
    public abstract PolicyEngineResource deletePolicyEngine(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid
    );

}
