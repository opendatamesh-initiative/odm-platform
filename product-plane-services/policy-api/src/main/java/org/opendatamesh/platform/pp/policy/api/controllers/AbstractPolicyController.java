package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
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
    public abstract Page<PolicyRes> getPolicies(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicySearchOptions searchOptions
    );

    @GetMapping(value = "/{uuid}")
    public abstract PolicyRes getPolicy(
            @Parameter(description = "", required = true)
            @PathVariable(value = "uuid") String uuid
    );

    @PostMapping
    public abstract PolicyRes createPolicy(
            @Parameter(description = "")
            @RequestBody PolicyRes policy
    );

    @PutMapping(value = "/{uuid}")
    public abstract PolicyRes modifyPolicy(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid,
            @Parameter(description = "")
            @RequestBody PolicyRes policy
    );

    @DeleteMapping(value = "/{uuid}")
    public abstract PolicyRes deletePolicy(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid
    );
}
