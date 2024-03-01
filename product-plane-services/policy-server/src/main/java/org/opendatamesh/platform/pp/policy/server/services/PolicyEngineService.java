package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyEngineMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyEngineRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PolicyEngineService extends GenericMappedAndFilteredCrudService<PolicyEngineSearchOptions, PolicyEngineResource, PolicyEngine, Long> {

    @Autowired
    private PolicyEngineRepository repository;

    @Autowired
    private PolicyEngineMapper mapper;

    @Override
    protected void validate(PolicyEngine objectToValidate) {
        if (!StringUtils.hasText(objectToValidate.getAdapterUrl())) {
            throw new BadRequestException(); //TODO
        }
        if (!StringUtils.hasText(objectToValidate.getName())) {
            throw new BadRequestException(); //TODO
        }
    }

    @Override
    protected void reconcile(PolicyEngine objectToReconcile) {
        // No reconcile action needed
    }


    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<PolicyEngine, Long> getRepository() {
        return repository;
    }

    @Override
    protected Specification<PolicyEngine> getSpecFromFilters(PolicyEngineSearchOptions filters) {
        return null;
    }

    @Override
    protected PolicyEngineResource toRes(PolicyEngine entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected PolicyEngine toEntity(PolicyEngineResource resource) {
        return mapper.toEntity(resource);
    }
}
