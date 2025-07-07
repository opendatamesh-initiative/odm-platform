package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.internals;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Embeddable
public class LifecycleInfo {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns({
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    @OrderBy(value = "id ASC")
    private List<LifecycleTaskInfo> tasksInfo = new ArrayList<LifecycleTaskInfo>();
}
