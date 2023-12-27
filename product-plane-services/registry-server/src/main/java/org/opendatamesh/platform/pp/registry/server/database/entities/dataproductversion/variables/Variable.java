package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.variables;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "Variables")
@Table(name = "DPV_VARIABLES", schema="ODMREGISTRY")
public class Variable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATA_PRODUCT_ID")
    private String dataProductId;

    @Column(name = "DATA_PRODUCT_VERSION")
    private String dataProductVersion;

    @Column(name = "VARIABLE_NAME")
    private String variableName;

    @Column(name = "VARIABLE_VALUE")
    private String variableValue;

}