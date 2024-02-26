package org.opendatamesh.platform.pp.policy.server.database.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "PolicyHistory")
@Table(name = "POLICIES_HISTORY", schema="ODMPOLICY")
public class PolicyHistory {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "POLICY_UUID")
    private String policyUUID;

    @Column(name = "RAW_CONTENT")
    private String rawContent;

}
