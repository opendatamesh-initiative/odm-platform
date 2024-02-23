package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyEvaluationResultMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyEvaluationResultRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

//TODO change id type when configured
@Service
public class PolicyEvaluationResultService extends GenericMappedAndFilteredCrudService<PolicyEvaluationResultSearchOptions, PolicyEvaluationResultResource, PolicyEvaluationResult, String> {

    //@Autowired
    private PolicyEvaluationResultRepository repository;

    //@Autowired
    private PolicyEvaluationResultMapper mapper;


    @Override
    protected void validate(PolicyEvaluationResult objectToValidate) {

    }

    @Override
    protected void reconcile(PolicyEvaluationResult objectToReconcile) {

    }

    @Override
    protected String getIdentifier(PolicyEvaluationResult object) {
        return null;
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<PolicyEvaluationResult, String> getRepository() {
        return null;
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
