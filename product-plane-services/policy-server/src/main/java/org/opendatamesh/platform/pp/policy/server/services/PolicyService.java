package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PolicyService extends GenericMappedAndFilteredCrudService<PolicySearchOptions, PolicyResource, Policy, Long> {

    @Autowired
    private PolicyRepository repository;

    @Autowired
    private PolicyMapper mapper;

    @Autowired
    private PolicyEngineService policyEngineService;

    public PolicyResource findOnePolicyResource(Long rootId) {
        return toRes(findOnePolicy(rootId));
    }

    public Policy findOnePolicy(Long rootId) {
        final Policy policy = repository.findByRootIdAndIsLastVersionTrue(rootId);
        if (policy == null) {
            throw new NotFoundException();//TODO
        }
        return policy;
    }

    public PolicyResource createPolicyResource(PolicyResource policyToCreate) {
        PolicyResource createdPolicy = createResource(policyToCreate);
        createdPolicy.setRootId(createdPolicy.getId());
        createdPolicy.setIsLastVersion(true);
        return overwriteResource(createdPolicy.getId(), createdPolicy);
    }

    public PolicyResource overwritePolicyResource(Long rootId, PolicyResource policy) {
        Policy lastVersionPolicy = findOnePolicy(rootId);
        lastVersionPolicy.setLastVersion(false);
        overwrite(lastVersionPolicy.getId(), lastVersionPolicy);

        policy.setRootId(rootId);
        policy.setIsLastVersion(true);
        return createResource(policy);
    }

    public void deletePolicy(Long rootId) {
        Policy policyToDelete = findOnePolicy(rootId);
        policyToDelete.setLastVersion(false);
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
        if(repository.existsByNameAndRootIdNot(policy.getName(), policy.getRootId())){
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
    protected Long getIdentifier(Policy object) {
        return object.getId();
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Policy, Long> getRepository() {
        return repository;
    }


    @Override
    protected Specification<Policy> getSpecFromFilters(PolicySearchOptions filters) {
        return null;
    }

    @Override
    protected PolicyResource toRes(Policy entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected Policy toEntity(PolicyResource resource) {
        return mapper.toEntity(resource);
    }
}
