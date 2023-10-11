package org.opendatamesh.platform.pp.blueprint.server.database.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity(name = "Blueprint")
@Table(name = "BLUEPRINTS", schema="ODMBLUEPRINT")
public class Blueprint {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "REPOSITORY_PROVIDER")
    private String repositoryProvider;

    @Column(name = "REPOSITORY_URL")
    private String repositoryUrl;

    @Column(name = "BLUEPRINT_PATH")
    private String blueprintPath;

    @Column(name = "TARGET_PATH")
    private String targetPath;

    @ElementCollection
    @CollectionTable(name = "BLUEPRINT_CONFIGS", joinColumns = @JoinColumn(name = "BLUEPRINT_ID"))
    @MapKeyColumn(name = "PARAMETER")
    @Column(name = "PARAMETER_VALUE")
    private Map<String, String> configurations;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}