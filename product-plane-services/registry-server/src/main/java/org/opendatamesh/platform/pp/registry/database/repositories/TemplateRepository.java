package org.opendatamesh.platform.pp.registry.database.repositories;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TemplateRepository extends JpaRepository<Template, Long>, JpaSpecificationExecutor<Template> {
}
