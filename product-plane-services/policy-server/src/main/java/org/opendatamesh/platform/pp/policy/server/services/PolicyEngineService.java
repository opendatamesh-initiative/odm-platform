package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicyEngineMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicyEngineRepository;
import org.opendatamesh.platform.core.commons.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
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

    protected PolicyEngineService() {
    }

    @Override
    protected void validate(PolicyEngine objectToValidate) {
        if (objectToValidate == null) {
            throw new BadRequestException(
                    PolicyApiStandardErrors.SC400_01_POLICY_ENGINE_IS_EMPTY,
                    "PolicyEngine object cannot be null"
            );
        }

        if (!StringUtils.hasText(objectToValidate.getAdapterUrl())) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                    "PolicyEngine adapterUrl cannot be null"
            );
        }
        if (!StringUtils.hasText(objectToValidate.getName())) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_01_POLICY_ENGINE_IS_INVALID,
                    "PolicyEngine name cannot be null"
            );
        }
    }

    @Override
    protected void beforeCreation(PolicyEngine policyEngine) {
        if (repository.existsByName(policyEngine.getName())) {
            throw new UnprocessableEntityException(
                    PolicyApiStandardErrors.SC422_05_POLICY_ENGINE_ALREADY_EXISTS,
                    "PolicyEngine with name [" + policyEngine.getName() + "] already exists"
            );
        }
    }

    @Override
    protected void beforeOverwrite(PolicyEngine objectToOverwrite) {
       if(objectToOverwrite.getId() == null) {
           // If ID is null, check only for name existence
           if(repository.existsByName(objectToOverwrite.getName())){
               throw new UnprocessableEntityException(
                       PolicyApiStandardErrors.SC422_05_POLICY_ENGINE_ALREADY_EXISTS,
                       "PolicyEngine with name [" + objectToOverwrite.getName() + "] already exists"
               );
           }
       } else {
           // If ID is not null, check for name existence excluding current entity
           if(repository.existsByNameAndIdIsNot(objectToOverwrite.getName(), objectToOverwrite.getId())){
               throw new UnprocessableEntityException(
                       PolicyApiStandardErrors.SC422_05_POLICY_ENGINE_ALREADY_EXISTS,
                       "PolicyEngine with name [" + objectToOverwrite.getName() + "] already exists"
               );
           }
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

    @Override
    protected Class<PolicyEngine> getEntityClass() {
        return PolicyEngine.class;
    }

    public PolicyEngine findByName(String name){
        return repository.findByName(name);
    }
}
