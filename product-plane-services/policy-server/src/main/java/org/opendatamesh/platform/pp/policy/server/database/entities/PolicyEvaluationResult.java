package org.opendatamesh.platform.pp.policy.server.database.entities;

import org.opendatamesh.platform.pp.policy.server.database.utils.TimestampedEntity;

import javax.persistence.*;

@Entity(name = "PolicyEvaluationResults")
@Table(name = "POLICY_EVALUATION_RESULTS", schema = "ODMPOLICY")
public class PolicyEvaluationResult extends TimestampedEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATA_PRODUCT_ID")
    private String dataProductId;

    @Column(name = "DATA_PRODUCT_VERSION")
    private String dataProductVersionNumber;

    @Column(name = "POLICY_ID")
    private Long policyId;

    @Column(name = "INPUT_OBJECT")
    private String inputObject;

    @Column(name = "OUTPUT_OBJECT")
    private String outputObject;

    @Column(name = "RESULT")
    private Boolean result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataProductId() {
        return dataProductId;
    }

    public void setDataProductId(String dataProductId) {
        this.dataProductId = dataProductId;
    }

    public String getDataProductVersionNumber() {
        return dataProductVersionNumber;
    }

    public void setDataProductVersionNumber(String dataProductVersionNumber) {
        this.dataProductVersionNumber = dataProductVersionNumber;
    }

    public String getInputObject() {
        return inputObject;
    }

}
