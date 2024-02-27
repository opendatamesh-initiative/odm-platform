package org.opendatamesh.platform.pp.policy.server.database.entities;

import org.opendatamesh.platform.pp.policy.server.database.utils.TimestampedEntity;

import javax.persistence.*;

@Entity(name = "PolicyEvaluationResults")
@Table(name = "POLICY_EVALUATION_RESULTS", schema = "ODMPOLICY")
public class PolicyEvaluationResult extends TimestampedEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATA_PRODUCT_ID")
    private String dataProductId;

    @Column(name = "DATA_PRODUCT_VERSION")
    private String dataProductVersionNumber;

    @Column(name = "POLICY_HISTORY_ID")
    private Long policyHistoryId;

    @Column(name = "POLICY_UUID")
    private String policyUUID;

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

    public Long getPolicyHistoryId() {
        return policyHistoryId;
    }

    public void setPolicyHistoryId(Long policyHistoryId) {
        this.policyHistoryId = policyHistoryId;
    }

    public String getPolicyUUID() {
        return policyUUID;
    }

    public void setPolicyUUID(String policyUUID) {
        this.policyUUID = policyUUID;
    }

    public String getInputObject() {
        return inputObject;
    }

    public void setInputObject(String inputObject) {
        this.inputObject = inputObject;
    }

    public String getOutputObject() {
        return outputObject;
    }

    public void setOutputObject(String outputObject) {
        this.outputObject = outputObject;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
