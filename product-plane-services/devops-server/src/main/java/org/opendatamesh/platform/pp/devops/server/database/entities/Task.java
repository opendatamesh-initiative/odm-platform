package org.opendatamesh.platform.pp.devops.server.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;

@Data
@Entity(name = "Task")
@Table(name = "TASKS", schema = "ODMDEVOPS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task  {
    
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ACTIVITY_ID")
    Long activityId;  

    @Column(name = "EXECUTOR_REF")
    String executorRef;  
    
    @Column(name = "TEMPLATE")
    String template;  

    @Column(name = "CONFIGURATIONS")
    String configurations;  

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    ActivityTaskStatus status;

    @Column(name = "RESULTS")
    String results;

    @Column(name = "ERRORS")
    String errors;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "STARTED_AT")
    private Date startedAt;

    @Column(name = "FINISHED_AT")
    private Date finishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
