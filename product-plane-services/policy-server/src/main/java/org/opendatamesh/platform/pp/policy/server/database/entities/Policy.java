package org.opendatamesh.platform.pp.policy.server.database.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity(name = "Policy")
@Table(name = "POLICIES", schema="ODMPOLICY")
public class Policy {

    @Id
    @Column(name = "UUID")
    private String id;

    @Column(name = "POLICY_ENGINE_UUID")
    private String policyEngineUUID;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "BLOCKING_FLAG")
    private Boolean blockingFlag;

    @Column(name = "SUITE")
    private String suite;

    @Column(name = "RAW_CONTENT")
    private String rawContent;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

}
