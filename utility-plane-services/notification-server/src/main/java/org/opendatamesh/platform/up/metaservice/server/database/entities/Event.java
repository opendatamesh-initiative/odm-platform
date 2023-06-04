package org.opendatamesh.platform.up.metaservice.server.database.entities;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Embeddable
@Data
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

}
