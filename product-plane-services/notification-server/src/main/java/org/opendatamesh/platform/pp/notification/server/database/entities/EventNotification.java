package org.opendatamesh.platform.pp.notification.server.database.entities;

import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.opendatamesh.platform.pp.notification.server.database.entities.embedded.Event;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Notification")
@Table(name = "NOTIFICATIONS", schema = "ODMNOTIFICATION")
public class EventNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Embedded
    private Event event;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private EventNotificationStatus status;

    @Column(name = "PROCESSING_OUTPUT")
    private String processingOutput;

    @Column(name = "RECEIVED_AT")
    private Date receivedAt;

    @Column(name = "PROCESSED_AT")
    private Date processedAt;

    @PrePersist
    protected void onCreate() {
        receivedAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventNotificationStatus getStatus() {
        return status;
    }

    public void setStatus(EventNotificationStatus status) {
        this.status = status;
    }

    public String getProcessingOutput() {
        return processingOutput;
    }

    public void setProcessingOutput(String processingOutput) {
        this.processingOutput = processingOutput;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }
}
