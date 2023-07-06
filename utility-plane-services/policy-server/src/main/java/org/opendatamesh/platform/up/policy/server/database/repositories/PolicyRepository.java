package org.opendatamesh.platform.up.policy.server.database.repositories;

import org.opendatamesh.platform.up.policy.server.database.entities.PolicyEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends PagingAndSortingRepository<PolicyEntity, String> {
}