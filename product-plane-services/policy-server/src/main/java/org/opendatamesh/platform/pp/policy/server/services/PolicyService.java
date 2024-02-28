package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
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

    @Override
    protected Policy findById(Long rootId) {
        return repository.findByRootIdAndIsLastVersionTrue(rootId);
    }

    @Override
    protected void beforeCreation(Policy objectToCreate) {
        if (objectToCreate.getId() != null) {
            throw new BadRequestException();
        }
        if (objectToCreate.getRootId() != null) {
            throw new BadRequestException();
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
            throw new BadRequestException();
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
        if (!StringUtils.hasText(policy.getName())) {
            throw new BadRequestException(); //TODO
        }
        if (policy.getPolicyEngineId() == null || policy.getPolicyEngine() == null) {
            throw new BadRequestException(); //TODO
        }
        if (repository.existsByNameAndRootIdNot(policy.getName(), policy.getRootId())) {
            throw new BadRequestException(); //TODO policy with this name already exists
        }
    }

    @Override
    protected void reconcile(Policy objectToReconcile) {
        if (objectToReconcile.getPolicyEngineId() != null) {//TODO understand if a policy can exist without an engine
            PolicyEngine policyEngine = policyEngineService.findOne(objectToReconcile.getPolicyEngineId());
            objectToReconcile.setPolicyEngine(policyEngine);
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
                .orElseThrow(() -> new NotFoundException(PolicyApiStandardErrors.SC404_01_RESOURCE_NOT_FOUND, "Policy with version=" + versionId + " not found"));
    }

    public PolicyResource findPolicyResourceVersion(Long versionId) {
        return mapper.toRes(findPolicyVersion(versionId));
    }
}
