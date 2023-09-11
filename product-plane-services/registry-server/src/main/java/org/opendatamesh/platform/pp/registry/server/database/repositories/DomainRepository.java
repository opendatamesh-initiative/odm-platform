package org.opendatamesh.platform.pp.registry.server.database.repositories;

import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DomainRepository extends JpaRepository<Domain, String>, JpaSpecificationExecutor<Domain> {

    Optional<Domain> findByFullyQualifiedName(String fqn);
}
