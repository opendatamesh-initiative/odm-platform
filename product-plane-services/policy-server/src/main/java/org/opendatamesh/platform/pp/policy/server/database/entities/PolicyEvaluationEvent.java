package org.opendatamesh.platform.pp.policy.server.database.entities;

import javax.persistence.*;

@Entity
@Table(name = "POLICIES_EVALUATION_EVENTS", schema = "ODMPOLICY")
public class PolicyEvaluationEvent {
    @Id
    @Column(name = "SEQUENCE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequenceId;

    @Column(name = "POLICY_ID", insertable = false, updatable = false)
    private Long policyId;

    @ManyToOne
    @JoinColumn(name = "POLICY_ID")
    private Policy policy;

    @Column(name = "EVENT")
    private String event;

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
