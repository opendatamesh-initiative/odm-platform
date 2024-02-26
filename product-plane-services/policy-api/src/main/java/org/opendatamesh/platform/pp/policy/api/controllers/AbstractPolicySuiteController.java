package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(
        value = "/policy-suites"
)
public abstract class AbstractPolicySuiteController {

    @GetMapping
    public abstract Page<PolicySuiteRes> getPolicySuites(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicySuiteSearchOptions searchOptions
    );

    @GetMapping(value = "/{uuid}")
    public abstract PolicySuiteRes getPolicySuite(
            @Parameter(description = "", required = true)
            @PathVariable(value = "uuid") String uuid
    );

    @PostMapping
    public abstract PolicySuiteRes createPolicySuite(
            @Parameter(description = "")
            @RequestBody PolicySuiteRes policySuite
    );

    @PutMapping(value = "/{uuid}")
    public abstract PolicySuiteRes modifyPolicySuite(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid,
            @Parameter(description = "")
            @RequestBody PolicySuiteRes policySuite
    );

    @DeleteMapping(value = "/{uuid}")
    public abstract PolicySuiteRes deletePolicySuite(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid
    );
}
