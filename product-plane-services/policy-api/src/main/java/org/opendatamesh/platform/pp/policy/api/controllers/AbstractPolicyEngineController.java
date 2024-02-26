package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(
        value = "/policy-engines"
)
public abstract class AbstractPolicyEngineController {

    @GetMapping
    public abstract Page<PolicyEngineRes> getPolicyEngines(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicyEngineSearchOptions searchOptions
    );

    @GetMapping(value = "/{uuid}")
    public abstract PolicyEngineRes getPolicyEngine(
            @Parameter(description = "", required = true)
            @PathVariable(value = "uuid") String uuid
    );

    @PostMapping
    public abstract PolicyEngineRes createPolicyEngine(
            @Parameter(description = "")
            @RequestBody PolicyEngineRes policyEngine
    );

    @PutMapping(value = "/{uuid}")
    public abstract PolicyEngineRes modifyPolicyEngine(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid,
            @Parameter(description = "")
            @RequestBody PolicyEngineRes policyEngine
    );

    @DeleteMapping(value = "/{uuid}")
    public abstract PolicyEngineRes deletePolicyEngine(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid
    );
}
