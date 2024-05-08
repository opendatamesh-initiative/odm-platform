package org.opendatamesh.platform.pp.notification.server.database.entities;

import org.opendatamesh.platform.pp.notification.api.resources.enums.EventType;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Event")
@Table(name = "EVENTS", schema = "ODMNOTIFICATION")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "BEFORE_STATE")
    private String beforeState;

    @Column(name = "AFTER_STATE")
    private String afterState;

    @Column(name = "TIME")
    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(String beforeState) {
        this.beforeState = beforeState;
    }

    public String getAfterState() {
        return afterState;
    }

    public void setAfterState(String afterState) {
        this.afterState = afterState;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
