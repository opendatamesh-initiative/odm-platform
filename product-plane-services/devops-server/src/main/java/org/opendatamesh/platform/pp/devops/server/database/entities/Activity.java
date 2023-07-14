package org.opendatamesh.platform.pp.devops.server.database.entities;


import lombok.Data;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityType;

import java.util.Date;

@Data
public class Activity {
    Long id;

    private String dataProductId;

    private String dataProductVersion;

    ActivityType type;

    ActivityStatus status;

    String results;

    String errors;

    private Date startedAt;

    private Date finishedAt;
}