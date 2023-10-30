package org.opendatamesh.platform.pp.blueprint.server.database.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "ORGANIZATION")
    private String organization;

    @Column(name = "PROJECT_NAME")
    private String projectName;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Transient
    private String repoBaseUrl;

    @PrePersist
    protected void onCreate() {
        createdAt = now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = now();
    }

    @PostLoad
    protected void onLoad() {
        int lastSlashIndex = repositoryUrl.lastIndexOf("/");
        repoBaseUrl = repositoryUrl.substring(0, lastSlashIndex + 1);
    }


    private LocalDateTime now() {
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond(), 0);
        return now;
    }

}