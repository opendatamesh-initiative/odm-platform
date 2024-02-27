package org.opendatamesh.platform.pp.policy.server.database.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name = "PolicyEngine")
@Table(name = "POLICY_ENGINES", schema="ODMPOLICY")
public class PolicyEngine {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "ADAPTER_URL")
    private String adapterUrl;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

}
