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
    private String dataProductVersion;

    @Column(name = "POLICY_ID", insertable = false, updatable = false)
    private Long policyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ID")
    private Policy policy;

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

    public String getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(String dataProductVersionNumber) {
        this.dataProductVersion = dataProductVersionNumber;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
        Policy p = new Policy();
        p.setId(policyId);
        this.policy = p;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
        if (policy != null) {
            this.policyId = policy.getId();
        }
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
