package org.opendatamesh.platform.pp.policy.server.database.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name = "PolicyEvaluationResults")
@Table(name = "POLICY_EVALUATION_RESULTS", schema="ODMPOLICY")
public class PolicyEvaluationResult {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

}
