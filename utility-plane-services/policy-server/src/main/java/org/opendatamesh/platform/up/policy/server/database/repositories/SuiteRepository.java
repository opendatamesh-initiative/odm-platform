package org.opendatamesh.platform.up.policy.server.database.repositories;

import org.opendatamesh.platform.up.policy.server.database.entities.SuiteEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuiteRepository extends PagingAndSortingRepository<SuiteEntity, String> {
}