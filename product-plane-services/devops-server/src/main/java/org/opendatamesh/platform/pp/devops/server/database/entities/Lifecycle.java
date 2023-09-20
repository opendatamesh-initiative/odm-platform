package org.opendatamesh.platform.pp.devops.server.database.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity(name = "Lifecycle")
@Table(name = "LIFECYCLES", schema = "ODMDEVOPS")
public class Lifecycle {



}
