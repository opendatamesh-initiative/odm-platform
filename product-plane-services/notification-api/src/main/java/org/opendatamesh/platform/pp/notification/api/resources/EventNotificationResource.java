package org.opendatamesh.platform.pp.notification.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;

import java.sql.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventNotificationResource implements Cloneable {

    @JsonProperty("id")
    @Schema(description = "Auto generated ID of the Notification")
    private Long id;

    @JsonProperty("event")
    @Schema(description = "Event object of the Notification", required = true)
    private EventResource event;

    @JsonProperty("status")
    @Schema(description = "Status of the Notification")
    private EventNotificationStatus status;

    @JsonProperty("processingOutput")
    @Schema(description = "Output of the Notification processing phase")
    private String processingOutput;

    @JsonProperty("observer")
    @Schema(description = "Observer that handle this specific notification")
    private ObserverResource observer;

    @JsonProperty("receivedAt")
    @Schema(description = "Timestamp of the Notification reception")
    private Date receivedAt;

    @JsonProperty("processedAt")
    @Schema(description = "Timestamp of the Notification processing phase")
    private Date processedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventResource getEvent() {
        return event;
    }

    public void setEvent(EventResource event) {
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

    public ObserverResource getObserver() {
        return observer;
    }

    public void setObserver(ObserverResource observer) {
        this.observer = observer;
    }

}