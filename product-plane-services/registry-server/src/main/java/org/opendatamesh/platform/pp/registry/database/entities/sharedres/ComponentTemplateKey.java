package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class ComponentTemplateKey implements Serializable {

    @Column(name = "APP_COMPONENT_ID")
    Long appComponentId;

    @Column(name = "TEMPLATE_ID")
    Long templateId;

}
