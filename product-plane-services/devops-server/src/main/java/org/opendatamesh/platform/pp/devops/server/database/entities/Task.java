package org.opendatamesh.platform.pp.devops.server.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.utils.HashMapConverter;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private Long activityId;

    @Column(name = "EXECUTOR_REF")
    private String executorRef;
    
    @Column(name = "TEMPLATE")
    private String template;

    @Column(name = "CONFIGURATIONS")
    private String configurations;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private ActivityTaskStatus status;

    @Column(name = "RESULTS")
    private String results;

    @Column(name = "ERRORS")
    private String errors;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "STARTED_AT")
    private LocalDateTime startedAt;

    @Column(name = "FINISHED_AT")
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
