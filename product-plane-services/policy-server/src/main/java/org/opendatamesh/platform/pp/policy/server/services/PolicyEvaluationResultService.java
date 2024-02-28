package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
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

    @Override
    protected void validate(PolicyEvaluationResult evaluationResult) {
        if (evaluationResult.getPolicyId() == null) {
            throw new BadRequestException();
        }
        if (evaluationResult.getResult() == null) {
            throw new BadRequestException();
        }
    }

    @Override
    protected void reconcile(PolicyEvaluationResult evaluationResult) {
        if (evaluationResult.getPolicyId() != null) {
            Policy policy = policyService.findPolicyVersion(evaluationResult.getPolicyId());
            if(Boolean.FALSE.equals(policy.getLastVersion())){
                throw new BadRequestException();//TODO Invalid result???
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
