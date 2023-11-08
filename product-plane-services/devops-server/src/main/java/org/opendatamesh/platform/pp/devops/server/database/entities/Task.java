package org.opendatamesh.platform.pp.devops.server.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.*;

import org.opendatamesh.platform.core.dpds.utils.HashMapConverter;
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

    @Column(name = "RESULTS", length=5000)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> results;

    @Column(name = "ERRORS")
    String errors;

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
