package org.opendatamesh.platform.pp.blueprint.server.database.entities;

import lombok.Data;

import javax.persistence.*;
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

    @Column(name = "REPOSITORY_BASE_URL")
    private String repositoryBaseUrl;

    @Column(name = "BLUEPRINT_REPO")
    private String blueprintRepo;

    @Column(name = "TARGET_REPO")
    private String targetRepo;

    /*@ElementCollection
    @CollectionTable(name = "BLUEPRINT_CONFIGS", joinColumns = @JoinColumn(name = "BLUEPRINT_ID"))
    @MapKeyColumn(name = "PARAMETER")
    @Column(name = "PARAMETER_VALUE")
    private Map<String, String> configurations;*/

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = now();
    }

    private LocalDateTime now() {
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond(), 0);
        return now;
    }

}