package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@Entity(name = "ApiStandardDefinition")
@Table(name = "DPV_API_STD_DEFS", schema="ODMREGISTRY")
public class ApiStandardDefinition extends StandardDefinition {

}