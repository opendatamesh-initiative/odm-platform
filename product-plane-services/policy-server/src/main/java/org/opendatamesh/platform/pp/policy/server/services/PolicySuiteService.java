package org.opendatamesh.platform.pp.policy.server.services;

import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteSearchOptions;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicySuite;
import org.opendatamesh.platform.pp.policy.server.database.mappers.PolicySuiteMapper;
import org.opendatamesh.platform.pp.policy.server.database.repositories.PolicySuiteRepository;
import org.opendatamesh.platform.pp.policy.server.database.utils.PagingAndSortingAndSpecificationExecutorRepository;
import org.opendatamesh.platform.pp.policy.server.services.utils.GenericMappedAndFilteredCrudService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

//TODO change id type when configured
@Service
public class PolicySuiteService extends GenericMappedAndFilteredCrudService<PolicySuiteSearchOptions, PolicySuiteResource, PolicySuite, String> {

    //@Autowired
    private PolicySuiteRepository repository;

    //@Autowired
    private PolicySuiteMapper mapper;

    @Override
    protected void validate(PolicySuite objectToValidate) {

    }

    @Override
    protected void reconcile(PolicySuite objectToReconcile) {

    }

    @Override
    protected String getIdentifier(PolicySuite object) {
        return null;
    }

    @Override
    protected PagingAndSortingAndSpecificationExecutorRepository<PolicySuite, String> getRepository() {
        return null;
    }


    @Override
    protected Specification<PolicySuite> getSpecFromFilters(PolicySuiteSearchOptions filters) {
        return null;
    }

    @Override
    protected PolicySuiteResource toRes(PolicySuite entity) {
        return mapper.toRes(entity);
    }

    @Override
    protected PolicySuite toEntity(PolicySuiteResource resource) {
        return mapper.toEntity(resource);
    }
}
