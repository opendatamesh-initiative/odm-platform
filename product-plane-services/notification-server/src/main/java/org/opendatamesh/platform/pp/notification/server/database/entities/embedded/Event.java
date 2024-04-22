package org.opendatamesh.platform.pp.notification.server.database.entities.embedded;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
public class Event {

    @Column(name = "EVENT_ID")
    private Long id;

    @Column(name = "EVENT_TYPE")
    private String type;

    @Column(name = "EVENT_ENTITY_ID")
    private String entityId;

    @Column(name = "EVENT_BEFORE_STATE")
    private String beforeState;

    @Column(name = "EVENT_AFTER_STATE")
    private String afterState;

    @Column(name = "EVENT_TIME")
    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
