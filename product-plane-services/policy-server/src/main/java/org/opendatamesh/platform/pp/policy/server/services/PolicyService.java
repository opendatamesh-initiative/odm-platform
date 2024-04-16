package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.SpecsUtils;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyService extends GenericMappedAndFilteredCrudService<PolicySearchOptions, PolicyResource, Policy, Long> {

    @Autowired
    private PolicyRepository repository;

    @Autowired
    private PolicyMapper mapper;

    @Autowired
    private PolicyEngineService policyEngineService;

    protected PolicyService() {

    }

    @Override
    protected Policy findById(Long rootId) {
        return repository.findByRootIdAndIsLastVersionTrue(rootId);
    }

    @Override
    protected void beforeCreation(Policy objectToCreate) {
        if (repository.existsByName(objectToCreate.getName())) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_04_POLICY_ALREADY_EXISTS,
                    "Policy with name [" + objectToCreate.getName() + "] already exists"
            );
        }
        if (objectToCreate.getId() != null) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                    "Impossible to create a Policy with an explicit Id"
            );
        }
        if (objectToCreate.getRootId() != null) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                    "Impossible to create a Policy with an explicit rootId"
            );
        }
    }

    @Override
    protected void afterCreation(Policy objectToCreate, Policy result) {
        result.setRootId(result.getId());
        result.setLastVersion(Boolean.TRUE);
    }

    @Override
    protected void beforeOverwrite(Policy policy) {
        if (policy.getRootId() == null) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                    "Impossible to update a Policy without a rootID"
            );
        }
        Policy lastVersionPolicy = findOne(policy.getRootId());
        lastVersionPolicy.setLastVersion(Boolean.FALSE);
        policy.setLastVersion(Boolean.TRUE);
    }


    public void logicalDelete(Long rootId) {
        Policy policyToDelete = findOne(rootId);
        policyToDelete.setLastVersion(Boolean.FALSE);
        repository.save(policyToDelete);
    }


    @Override
    protected void validate(Policy policy) {
        if (policy == null) {
            throw new BadRequestException(
                    PolicyApiStandardErrors.SC400_02_POLICY_IS_EMPTY,
                    "Policy object cannot be null"
            );
        }
        if (!StringUtils.hasText(policy.getName())) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                    "Policy name cannot be null"
            );
        }
        if (policy.getPolicyEngine() == null) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_02_POLICY_IS_INVALID,
                    "Policy policyEngineId or PolicyEngine object cannot be null"
            );
        }
        if (repository.existsByNameAndRootIdNot(policy.getName(), policy.getRootId())) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_04_POLICY_ALREADY_EXISTS,
                    "Policy with name [" + policy.getName() + "] already exists with a differet rootID"
            );
        }
    }

    @Override
    protected void reconcile(Policy objectToReconcile) {
        if (objectToReconcile.getPolicyEngine().getId() != null) {
            PolicyEngine policyEngine = policyEngineService.findOne(objectToReconcile.getPolicyEngineId());
            objectToReconcile.setPolicyEngine(policyEngine);
        }
        else if (StringUtils.hasText(objectToReconcile.getPolicyEngine().getName()) && objectToReconcile.getPolicyEngine().getId() == null) {
            objectToReconcile.setPolicyEngine(policyEngineService.findByName(objectToReconcile.getPolicyEngine().getName()));
        }
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Policy, Long> getRepository() {
        return repository;
    }

    @Override
    protected Specification<Policy> getSpecFromFilters(PolicySearchOptions filters) {
        List<Specification<Policy>> specifications = new ArrayList<>();
        specifications.add(PolicyRepository.Specs.hasLastVersion(Boolean.TRUE));

        if(StringUtils.hasText(filters.getEvaluationEvent())) {
            specifications.add(PolicyRepository.Specs.hasEvaluationEvent(filters.getEvaluationEvent()));
        }

        return SpecsUtils.combineWithAnd(specifications);
    }

    @Override
    protected PolicyResource toRes(Policy entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected Policy toEntity(PolicyResource resource) {
        return mapper.toEntity(resource);
    }

    public Policy findPolicyVersion(Long versionId) {
        return repository
                .findById(versionId)
                .orElseThrow(() -> new NotFoundException(
                        PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND,
                        "Policy with ID [" + versionId + "] not found")
                );
    }

    public PolicyResource findPolicyResourceVersion(Long versionId) {
        return mapper.toRes(findPolicyVersion(versionId));
    }

    @Override
    protected Class<Policy> getEntityClass() {
        return Policy.class;
    }

}
