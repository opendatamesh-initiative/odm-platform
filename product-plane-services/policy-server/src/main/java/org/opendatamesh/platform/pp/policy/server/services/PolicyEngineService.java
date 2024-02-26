package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyEngineMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyEngineRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

//TODO change id type when configured
@Service
public class PolicyEngineService extends GenericMappedAndFilteredCrudService<PolicyEngineSearchOptions, PolicyEngineRes, PolicyEngine, String> {

    //@Autowired
    private PolicyEngineRepository repository;

    //@Autowired
    private PolicyEngineMapper mapper;

    @Override
    protected void validate(PolicyEngine objectToValidate) {

    }

    @Override
    protected void reconcile(PolicyEngine objectToReconcile) {

    }

    @Override
    protected String getIdentifier(PolicyEngine object) {
        return null;
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<PolicyEngine, String> getRepository() {
        return repository;
    }

    @Override
    protected Specification<PolicyEngine> getSpecFromFilters(PolicyEngineSearchOptions filters) {
        return null;
    }

    @Override
    protected PolicyEngineRes toRes(PolicyEngine entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected PolicyEngine toEntity(PolicyEngineRes resource) {
        return mapper.toEntity(resource);
    }
}
