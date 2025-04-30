package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.core.commons.database.utils.SpecsUtils;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.mappers.EntitiesToResources;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyRepository;
import org.opendatamesh.platform.pp.policy.server.services.proxies.NotificationProxy;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
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
    @Autowired
    private NotificationProxy notificationProxy;
    @Autowired
    private TransactionTemplate transactionTemplate;


    protected PolicyService() {

    }

    @Override
    protected void beforeCreation(Policy objectToCreate) {
        if (repository.existsByNameAndIsLastVersionTrue(objectToCreate.getName())) {
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
        Policy lastVersionPolicy = repository.findByRootIdAndIsLastVersionTrue(policy.getRootId());
        if (lastVersionPolicy != null) {
            lastVersionPolicy.setLastVersion(Boolean.FALSE);
        }
        policy.setLastVersion(Boolean.TRUE);
    }

    public PolicyResource findOneResourceByRootIdAndIsLastVersion(Long rootId) {
        return mapper.toRes(findOneByRootIdAndIsLastVersion(rootId));
    }

    public Policy findOneByRootIdAndIsLastVersion(Long rootId) {
        Policy policy = repository.findByRootIdAndIsLastVersionTrue(rootId);
        if (policy == null) {
            throw new NotFoundException(
                    PolicyApiStandardErrors.getNotFoundError(EntitiesToResources.getResourceClassName(getEntityClass())),
                    "Resource with root ID [" + rootId + "] not found"
            );
        }
        return policy;
    }

    public void logicalDelete(Long rootId) {
        Policy policyToDelete = findOneByRootIdAndIsLastVersion(rootId);
        transactionTemplate.executeWithoutResult(status -> {
            policyToDelete.setLastVersion(Boolean.FALSE);
            repository.save(policyToDelete);
        });
        this.afterDeleteCommit(policyToDelete);
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
        if (repository.existsByNameAndIsLastVersionTrueAndRootIdNot(policy.getName(), policy.getRootId())) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_04_POLICY_ALREADY_EXISTS,
                    "Policy with name [" + policy.getName() + "] already exists with a different rootID"
            );
        }
    }

    @Override
    protected void reconcile(Policy objectToReconcile) {
        PolicyEngine policyEngine = null;
        if (objectToReconcile.getPolicyEngine().getId() != null) {
            policyEngine = policyEngineService.findOne(objectToReconcile.getPolicyEngineId());

        } else if (StringUtils.hasText(objectToReconcile.getPolicyEngine().getName()) && objectToReconcile.getPolicyEngine().getId() == null) {
            policyEngine = policyEngineService.findByName(objectToReconcile.getPolicyEngine().getName());
        }
        if (policyEngine == null) {
            throw new NotFoundException(
                    PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND,
                    "Policy Engine: [ " + objectToReconcile.getPolicyEngine() + "] not found"
            );
        }
        objectToReconcile.setPolicyEngine(policyEngine);
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Policy, Long> getRepository() {
        return repository;
    }

    @Override
    protected Specification<Policy> getSpecFromFilters(PolicySearchOptions filters) {
        List<Specification<Policy>> specifications = new ArrayList<>();
        if (StringUtils.hasText(filters.getEvaluationEvent())) {
            specifications.add(PolicyRepository.Specs.hasEvaluationEvent(filters.getEvaluationEvent()));
        }
        if (StringUtils.hasText(filters.getPolicyEngineName())) {
            specifications.add(PolicyRepository.Specs.hasEngineName(filters.getPolicyEngineName()));
        }
        if (StringUtils.hasText(filters.getName())) {
            specifications.add(PolicyRepository.Specs.hasName(filters.getName()));
        }
        if (filters.getLastVersion() != null) {
            specifications.add(PolicyRepository.Specs.hasLastVersion(filters.getLastVersion()));
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

    @Override
    protected void afterCreationCommit(Policy createdEntity) {
        super.afterCreationCommit(createdEntity);
        notificationProxy.notifyPolicyCreated(mapper.toRes(createdEntity));
    }

    @Override
    protected void afterDeleteCommit(Policy entity) {
        super.afterDeleteCommit(entity);
        notificationProxy.notifyPolicyDeleted(mapper.toRes(entity));
    }

    @Override
    protected void afterOverwriteCommit(Policy overwrittenObject) {
        super.afterOverwriteCommit(overwrittenObject);
        notificationProxy.notifyPolicyUpdated(null, mapper.toRes(overwrittenObject));
    }
}
