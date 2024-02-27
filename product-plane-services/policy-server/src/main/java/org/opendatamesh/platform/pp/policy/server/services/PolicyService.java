package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
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

    @Override
    protected void validate(Policy policy) {
        if(!StringUtils.hasText(policy.getName())){

        }
        if(policy.getPolicyEngineId() == null || policy.getPolicyEngine() == null){

        }
    }

    @Override
    protected void reconcile(Policy objectToReconcile) {

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
