package org.opendatamesh.platform.up.policy.server.database.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name="SUITE", schema = "ODMPOLICY")
@Data
public class SuiteEntity {
    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private String id;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @ElementCollection
    private List<String> policies;

}