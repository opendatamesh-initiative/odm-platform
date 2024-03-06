package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyEvaluationResultMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyEvaluationResultRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PolicyEvaluationResultService extends GenericMappedAndFilteredCrudService<PolicyEvaluationResultSearchOptions, PolicyEvaluationResultResource, PolicyEvaluationResult, Long> {

    @Autowired
    private PolicyEvaluationResultRepository repository;

    @Autowired
    private PolicyEvaluationResultMapper mapper;

    @Autowired
    private PolicyService policyService;

    protected PolicyEvaluationResultService() {
        super(PolicyEvaluationResult.class);
    }

    @Override
    protected void validate(PolicyEvaluationResult evaluationResult) {
        if(evaluationResult == null) {
            throw new BadRequestException(
                    PolicyApiStandardErrors.SC400_03_POLICY_EVALUATION_RESULT_IS_EMPTY,
                    "PolicyEvaluationResult object cannot be null"
            );
        }
        if (evaluationResult.getPolicyId() == null) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                    "PolicyEvaluationResult policyID cannot be null"
            );
        }
        if (evaluationResult.getResult() == null) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                    "PolicyEvaluationResult result cannot be null"
            );
        }
    }

    @Override
    protected void reconcile(PolicyEvaluationResult evaluationResult) {
        if (evaluationResult.getPolicyId() != null) {
            Policy policy = policyService.findPolicyVersion(evaluationResult.getPolicyId());
            if(Boolean.FALSE.equals(policy.getLastVersion())){
                throw new UnprocessableEntityException(
                        PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                        "The policy with ID ["+ evaluationResult.getPolicyId() + "] is inactive. "
                                + "Cannot add a result to an inactive policy"
                );
            }
            evaluationResult.setPolicy(policy);
        }
    }


    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<PolicyEvaluationResult, Long> getRepository() {
        return repository;
    }


    @Override
    protected Specification<PolicyEvaluationResult> getSpecFromFilters(PolicyEvaluationResultSearchOptions filters) {
        return null;
    }

    @Override
    protected PolicyEvaluationResultResource toRes(PolicyEvaluationResult entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected PolicyEvaluationResult toEntity(PolicyEvaluationResultResource resource) {
        return mapper.toEntity(resource);
    }

}
