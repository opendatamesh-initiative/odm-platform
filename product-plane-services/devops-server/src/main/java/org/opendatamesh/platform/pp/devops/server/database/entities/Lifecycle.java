package org.opendatamesh.platform.pp.devops.server.database.entities;

import lombok.Data;
import org.opendatamesh.platform.core.dpds.utils.HashMapConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "Lifecycle")
@Table(name = "LIFECYCLES", schema = "ODMDEVOPS")
public class Lifecycle {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "DATA_PRODUCT_ID")
    private String dataProductId;

    @Column(name = "DATA_PRODUCT_VERSION")
    private String dataProductVersion;

    @Column(name = "STAGE")
    private String stage;

    @Column(name = "RESULTS")
    @Convert(converter = HashMapConverter.class)
    private String results;

    @Column(name = "STARTED_AT")
    private LocalDateTime startedAt;

    @Column(name = "FINISHED_AT")
    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        startedAt = now();
    }

    private LocalDateTime now() {
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond(), 0);
        return now;
    }

}
