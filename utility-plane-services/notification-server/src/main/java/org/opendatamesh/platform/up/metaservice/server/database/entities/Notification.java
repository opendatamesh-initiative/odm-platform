package org.opendatamesh.platform.up.metaservice.server.database.entities;

import lombok.Data;

import javax.persistence.*;

import org.opendatamesh.platform.up.notification.api.v1.resources.NotificationStatus;

import java.util.Date;

@Data
@Entity(name = "Notification")
@Table(name = "NOTIFICATION", schema = "PUBLIC")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Embedded
    private Event event;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

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

}