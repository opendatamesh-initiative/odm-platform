package org.opendatamesh.platform.pp.devops.server.database.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;

import lombok.Data;

@Data
@Entity(name = "Activity")
@Table(name = "ACTIVITIES", schema = "ODMDEVOPS")
public class Activity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "DATA_PRODUCT_ID")
    private String dataProductId;

    @Column(name = "DATA_PRODUCT_VERSION")
    private String dataProductVersion;

    @Column(name = "STAGE")
    String stage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    ActivityStatus status;

    @Column(name = "RESULTS")
    String results;

    @Column(name = "ERRORS")
    String errors;

    @Column(name = "CREATED_AT")
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "STARTED_AT")
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startedAt;

    @Column(name = "FINISHED_AT")
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = now();
    }

    private LocalDateTime now() {
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 
        now.getHour(), now.getMinute(), now.getSecond(), 0);
        return now;
    }
}