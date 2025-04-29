package org.opendatamesh.platform.pp.policy.server.resources.notificationservice;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

public class NotificationEventResource {
    private Long id;
    private String type;
    private String entityId;
    private JsonNode beforeState;
    private JsonNode afterState;
    private Date time;

    public NotificationEventResource() {
    }

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

    public JsonNode getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(JsonNode beforeState) {
        this.beforeState = beforeState;
    }

    public JsonNode getAfterState() {
        return afterState;
    }

    public void setAfterState(JsonNode afterState) {
        this.afterState = afterState;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "OdmEventResource{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", entityId='" + entityId + '\'' +
                ", beforeState=" + beforeState +
                ", afterState=" + afterState +
                ", time=" + time +
                '}';
    }
}
