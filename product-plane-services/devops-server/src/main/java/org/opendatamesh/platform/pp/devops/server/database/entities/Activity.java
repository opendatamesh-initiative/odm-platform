package org.opendatamesh.platform.pp.devops.server.database.entities;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.sound.sampled.Port;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;

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

    @Column(name = "TYPE")
    String type;
    
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