package org.opendatamesh.platform.pp.devops.server.database.entities;

import lombok.Data;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "ACTIVITY_ID")
    private List<Task> tasks = new ArrayList<Task>();

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

	public Task getTask(Long id) {
        Task task = null;

        for(Task t : tasks) {
            if(id.equals(t.getId())) task = t;
        }

		return task;
	}

	public Task getNextPlannedTask() {
		Task plannedTask = null;
        for (Task task : tasks) {
            if (task.getStatus().equals(TaskStatus.PLANNED)) {
                plannedTask = task;
                break;
            }
        }
        return plannedTask;
	}

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}