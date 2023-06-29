package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "ComponentTemplate")
@Table(name = "DPDS_COMPONENT_TEMPLATE", schema="PUBLIC")
public class ComponentTemplate {

    @EmbeddedId
    ComponentTemplateId id;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComponentTemplateId implements Serializable {

        @Column(name = "COMPONENT_ID")
        String componentId;

        @Column(name = "TEMPLATE_ID")
        Long templateId;

        @Column(name = "COMPONENT_TYPE")
        String componentType;

        @Column(name = "INFO_TYPE")
        String infoType;

    }

}
