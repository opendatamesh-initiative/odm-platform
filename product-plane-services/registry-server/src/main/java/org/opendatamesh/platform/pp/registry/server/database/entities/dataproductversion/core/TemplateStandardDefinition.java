package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@Entity(name = "TemplateStandardDefinition")
@Table(name = "DPV_TEMPLATE_STD_DEFS", schema="ODMREGISTRY")
public class TemplateStandardDefinition extends StandardDefinition {

}