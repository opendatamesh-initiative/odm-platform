package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity(name = "ComponentTemplate")
@Table(name = "DPDS_APP_COMPONENT_TEMPLATE", schema="PUBLIC")
public class ComponentTemplate {

    @EmbeddedId
    ComponentTemplateKey id;

}
