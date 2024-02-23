package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

//TODO change id type when configured
@Service
public class PolicyService extends GenericMappedAndFilteredCrudService<PolicySearchOptions, PolicyRes, Policy, String> {

    //@Autowired
    private PolicyRepository repository;

    //@Autowired
    private PolicyMapper mapper;

    @Override
    protected void validate(Policy objectToValidate) {

    }

    @Override
    protected void reconcile(Policy objectToReconcile) {

    }

    @Override
    protected String getIdentifier(Policy object) {
        return null;
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<Policy, String> getRepository() {
        return null;
    }


    @Override
    protected Specification<Policy> getSpecFromFilters(PolicySearchOptions filters) {
        return null;
    }

    @Override
    protected PolicyRes toRes(Policy entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected Policy toEntity(PolicyRes resource) {
        return mapper.toEntity(resource);
    }
}
