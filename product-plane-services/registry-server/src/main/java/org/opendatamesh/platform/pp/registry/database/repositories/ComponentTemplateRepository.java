package org.opendatamesh.platform.pp.registry.database.repositories;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ComponentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ComponentTemplateRepository extends JpaRepository<ComponentTemplate, Long>, JpaSpecificationExecutor<ComponentTemplate> {
}
