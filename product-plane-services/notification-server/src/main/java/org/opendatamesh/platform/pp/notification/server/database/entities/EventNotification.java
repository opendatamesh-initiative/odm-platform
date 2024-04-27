package org.opendatamesh.platform.pp.notification.server.database.entities;

import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "EventNotification")
@Table(name = "NOTIFICATIONS", schema = "ODMNOTIFICATION")
public class EventNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private EventNotificationStatus status;

    @Column(name = "PROCESSING_OUTPUT")
    private String processingOutput;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    @Column(name = "EVENT_ID", insertable = false, updatable = false)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OBSERVER_ID")
    private Observer observer;

    @Column(name = "OBSERVER_ID", insertable = false, updatable = false)
    private Long observerId;

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

    public Observer getObserver() {
        return observer;
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
        if (observer != null) {
            this.observerId = observer.getId();
        }
    }

    public Long getObserverId() {
        return observerId;
    }

    public void setObserverId(Long observerId) {
        this.observerId = observerId;
        Observer observer = new Observer();
        observer.setId(observerId);
        this.observer = observer;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
