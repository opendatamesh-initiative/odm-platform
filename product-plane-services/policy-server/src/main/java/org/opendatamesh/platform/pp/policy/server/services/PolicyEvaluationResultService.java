package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultShortResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResultShort;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyEvaluationResultMapper;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyEvaluationResultShortMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyEvaluationResultRepository;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyEvaluationResultShortRepository;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyEvaluationResultService extends GenericMappedAndFilteredCrudService<PolicyEvaluationResultSearchOptions, PolicyEvaluationResultResource, PolicyEvaluationResult, Long> {

    @Autowired
    private PolicyEvaluationResultRepository repository;

    @Autowired
    private PolicyEvaluationResultShortRepository shortRepository;

    @Autowired
    private PolicyEvaluationResultMapper mapper;

    @Autowired
    private PolicyEvaluationResultShortMapper shortMapper;

    @Autowired
    private PolicyService policyService;

    protected PolicyEvaluationResultService() {

    }

    @Override
    protected void validate(PolicyEvaluationResult evaluationResult) {
        if (evaluationResult == null) {
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
            if (Boolean.FALSE.equals(policy.getLastVersion())) {
                throw new UnprocessableEntityException(
                        PolicyApiStandardErrors.SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID,
                        "The policy with ID [" + evaluationResult.getPolicyId() + "] is inactive. "
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
        List<Specification<PolicyEvaluationResult>> specifications = new ArrayList<>();
        if (StringUtils.hasText(filters.getDataProductId())) {
            specifications.add(PolicyEvaluationResultRepository.Specs.hasDataProductId(filters.getDataProductId()));
        }
        if (StringUtils.hasText(filters.getDataProductVersion())) {
            specifications.add(PolicyEvaluationResultRepository.Specs.hasDataProductVersion(filters.getDataProductVersion()));
        }
        if (filters.getDaysFromLastCreated() != null && filters.getDaysFromLastCreated() >= 0) {
            repository.findTopByOrderByCreatedAtDesc()
                    .ifPresent(mostRecent -> {
                        Timestamp cutoffDate = Timestamp.valueOf(
                                mostRecent.getCreatedAt()
                                        .toLocalDateTime()
                                        .minusDays(filters.getDaysFromLastCreated())
                        );
                        specifications.add(PolicyEvaluationResultRepository.Specs.createdAtGreaterThanOrEqualTo(cutoffDate));
                    });
        }
        return SpecsUtils.combineWithAnd(specifications);
    }

    @Override
    protected PolicyEvaluationResultResource toRes(PolicyEvaluationResult entity) {
        return mapper.toRes(entity);
    }

    public Page<PolicyEvaluationResultShortResource> findAllShortResourcesFiltered(Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions) {
        Specification<PolicyEvaluationResultShort> spec = getShortSpecFromFilters(searchOptions);
        Page<PolicyEvaluationResultShort> entities = shortRepository.findAll(spec, pageable);
        return entities.map(shortMapper::toRes);
    }

    private Specification<PolicyEvaluationResultShort> getShortSpecFromFilters(PolicyEvaluationResultSearchOptions filters) {
        List<Specification<PolicyEvaluationResultShort>> specifications = new ArrayList<>();
        if (StringUtils.hasText(filters.getDataProductId())) {
            specifications.add(PolicyEvaluationResultShortRepository.Specs.hasDataProductId(filters.getDataProductId()));
        }
        if (filters.getDaysFromLastCreated() != null && filters.getDaysFromLastCreated() > 0) {
            repository.findTopByOrderByCreatedAtDesc()
                    .ifPresent(mostRecent -> {
                        Timestamp cutoffDate = Timestamp.valueOf(
                                mostRecent.getCreatedAt()
                                        .toLocalDateTime()
                                        .minusDays(filters.getDaysFromLastCreated())
                        );
                        specifications.add(
                                PolicyEvaluationResultShortRepository.Specs.createdAtGreaterThanOrEqualTo(cutoffDate)
                        );
                    });
        }
        return SpecsUtils.combineWithAnd(specifications);
    }

    @Override
    protected PolicyEvaluationResult toEntity(PolicyEvaluationResultResource resource) {
        return mapper.toEntity(resource);
    }

    @Override
    protected Class<PolicyEvaluationResult> getEntityClass() {
        return PolicyEvaluationResult.class;
    }

}
