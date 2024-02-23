package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(
        value = "/policy-evaluation-results"
)
public abstract class AbstractPolicyEvaluationResultController {

    @GetMapping
    public abstract Page<PolicyEvaluationResultResource> getPolicyEvaluationResults(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicyEvaluationResultSearchOptions searchOptions
    );

    @GetMapping(value = "/{uuid}")
    public abstract PolicyEvaluationResultResource getPolicyEvaluationResult(
            @Parameter(description = "", required = true)
            @PathVariable(value = "uuid") String uuid
    );

    @PostMapping
    public abstract PolicyEvaluationResultResource createPolicyEvaluationResult(
            @Parameter(description = "")
            @RequestBody PolicyEvaluationResultResource policyEvaluationResult
    );

    @PutMapping(value = "/{uuid}")
    public abstract PolicyEvaluationResultResource modifyPolicyEvaluationResult(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid,
            @Parameter(description = "")
            @RequestBody PolicyEvaluationResultResource policyEvaluationResult
    );

    @DeleteMapping(value = "/{uuid}")
    public abstract PolicyEvaluationResultResource deletePolicyEvaluationResult(
            @Parameter(description = "")
            @PathVariable(value = "uuid") String uuid
    );
}
